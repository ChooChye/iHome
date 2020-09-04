package com.example.ihome.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_security_options.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SecurityOptions : AppCompatActivity() {
    private val mdate: String = java.text.SimpleDateFormat("yyyyMMdd",java.util.Locale.getDefault()).format(java.util.Date())
    private val hour: String = java.text.SimpleDateFormat("HH", java.util.Locale.getDefault()).format(java.util.Date())
    private val mTime: String = java.text.SimpleDateFormat("mmss", java.util.Locale.getDefault()).format(java.util.Date())
    private val date = "PI_01_"+mdate
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL
    private var storageRef = FirebaseStorage.getInstance().getReference()
    var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)
    private lateinit var securityOptionsListener : ValueEventListener
    private lateinit var sensorData:String
    val SHARED_PREF : String? = null
    val SECURITY_VAL : String? = null

    private var savedPref: Int? = null

    private lateinit var folder: String
    private lateinit var reportList : ArrayList<Reports>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_options)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Security Options"
        actionBar.setDisplayHomeAsUpEnabled(true)
        loadData()
        showPic()
        if(savedPref == 1){
            getSensorData()
        }
        reportList = ArrayList()
        updateSensors()
        checkAlarm()
        security_switch_alarm.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked) {
                alarmController(1)
            }else{
                alarmController(0)
            }
        })
        security_switch_sensor.setOnCheckedChangeListener({ _ , isChecked ->
            if(isChecked){
                saveData(1)
            }else{
                saveData(0)
                stopService(Intent(this, SecurityService::class.java))
                myRefSens.removeEventListener(securityOptionsListener)
            }
        })
        security_button_camera.setOnClickListener(){
            takePic()
        }

    }

    private fun alarmController(control:Int){
        var map = mutableMapOf<String,Any>()
        map["buzzer"] = control
        myRef.updateChildren(map) //add into Firebase
    }

    private fun checkAlarm(){
       myRef.addValueEventListener(object : ValueEventListener {
           override fun onCancelled(p0: DatabaseError) {
               Log.d("TestingSecurity_READ", "${p0.toException()}")
           }
           override fun onDataChange(p0: DataSnapshot) {
               try{
                   if(p0.exists()){
                       val map: Map<String, Object> = p0.getValue() as Map<String, Object>
                       if(map["buzzer"].toString() == 1.toString()){
                           //if buzzer == 1 turn on switch
                           security_switch_alarm.isChecked = true
                       }
                   }
               }catch (e: IOException){
                   showLog("Error #312 $e")
               }
           }
       })
    }

    fun saveData(value : Int){
        try {
            val sharedPreferences : SharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt(SECURITY_VAL, value)
            editor.apply()
        }catch (e: IOException){
            showLog("Error saving to sharedPref #145 $e")
        }
    }

    fun loadData(){
        val sharedPreferences : SharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        savedPref = sharedPreferences.getInt(SECURITY_VAL, 0)
        //showLog(savedPref.toString())
    }

    fun updateSensors(){
        if(savedPref == 0){
            security_switch_sensor.isChecked = false
        }else{
            security_switch_sensor.isChecked = true
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun takePic(){
        val fileName = fileName()
        folder = "PI_01_CONTROL/cam_$fileName.jpg"
        Thread {
            showLog("TurnOnCamera")
            var map = mutableMapOf<String,Any>()
            map["camera"] = "1"
            myRef.updateChildren(map) //add into Firebase
            Thread.sleep(4000)
            showLog("TurnOffCamera | PI_01_CONTROL/cam_$fileName.jpg")
            map["camera"] = "0"
            myRef.updateChildren(map)
        }.start()
    }

    private fun showPic(){
        try{
            var file: File = File.createTempFile("image", "jpg")
            storageRef.child(folder).getFile(file)
                .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                    showLog("Inside")
                    val bitmap : Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    securityOptions_imageView_cam.setImageBitmap(bitmap)
                }).addOnFailureListener(OnFailureListener {
                    // Handle failed download
                    showLog("Fail to show pic #675")
                })
        }catch (e: IOException){
            showLog("Empty #q234")
        }
    }

    private fun getSensorData(){
        securityOptionsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLog("${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("Pot1").getValue().toString()
                        if(sensorData < 500.toString()){
                            val timeStamp = timeStamp()
                            reportList.add(Reports(timeStamp))
                            showLog("Intruder detected at $sensorData | $timeStamp")
                            updateReportView()
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(securityOptionsListener)
    }

    fun updateReportView(){
        var fullReport : String? = null
        for(data in reportList){
            fullReport = "${data.timestamp} - Intruder detected \n"
        }
        security_textView_reportList.text = fullReport
    }

    fun timeStamp() : String{
        val milliseconds = System.currentTimeMillis()
        val sdf = SimpleDateFormat("HH:mm:ss")
        val resultDate = Date(milliseconds)
        return sdf.format(resultDate)
    }

    fun fileName():Long{
        val milliseconds = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val resultTime = sdf.format(Date(milliseconds))
        val fileName = Math.round(resultTime.toDouble()/10.0) * 10

        return fileName
    }

    fun showLog(message: String){
        val TAG = "DebuggingIOT"
        Log.d(TAG, message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }

    override fun onDestroy() {
        showLog("SecurityOptionsOnDestroy")
        myRefSens.removeEventListener(securityOptionsListener)
        super.onDestroy()
    }
}