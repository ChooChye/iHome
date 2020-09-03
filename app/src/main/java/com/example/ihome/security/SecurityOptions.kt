package com.example.ihome.security

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_security.*
import kotlinx.android.synthetic.main.activity_security_options.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SecurityOptions : AppCompatActivity() {
    private val mdate: String = java.text.SimpleDateFormat("yyyyMMdd",java.util.Locale.getDefault()).format(java.util.Date())
    private val hour: String = java.text.SimpleDateFormat("HH", java.util.Locale.getDefault()).format(java.util.Date())
    private val date = "PI_01_"+mdate
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL
    //private var storageRef = FirebaseStorage.getInstance().getReference().child("PI_01_CONTROL")
    var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)
    private lateinit var sensorListener : ValueEventListener
    private lateinit var sensorData:String
    val SHARED_PREF : String? = null
    val SECURITY_VAL : String? = null
    val TAG = "DebuggingIOT"
    private var savedPref: Int? = null

    private lateinit var reportList : ArrayList<Reports>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_options)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Security Options"
        actionBar.setDisplayHomeAsUpEnabled(true)
        loadData()
        if(savedPref == 1){
            getSensorData()
        }
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
            }
        })

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

    fun takePic(cameraUI: Int){
        var map = mutableMapOf<String,Any>()
        map["camera"] = cameraUI
        myRef.updateChildren(map) //add into Firebase
    }

    private fun showPic(){
        try{
            //storageRef.getFile()
        }catch (e: IOException){
            showLog("")
        }
    }

    private fun getSensorData(){
        sensorListener = object : ValueEventListener {
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
                            reportList = ArrayList()
                            reportList.add(Reports(timeStamp))
                            showLog("Intruder detected at $sensorData | $timeStamp")
                            updateReportView()
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(sensorListener)
    }

    fun updateReportView(){
        for(data in reportList){
            security_textView_reportList.text = "Intruder detected at ${data.timestamp}"
        }
    }

    fun timeStamp() : String{
        //val reportTimeStamp: String = java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss",java.util.Locale.getDefault()).format(java.util.Date())
        val milliseconds = System.currentTimeMillis()
        val sdf = SimpleDateFormat("d MMM yyyy HH:mm:ss")
        val resultDate = Date(milliseconds)
        return sdf.format(resultDate)
    }

    fun showLog(message: String){
        Log.d(TAG, message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }

    override fun onDestroy() {
        myRefSens.removeEventListener(sensorListener)
        super.onDestroy()
    }
}