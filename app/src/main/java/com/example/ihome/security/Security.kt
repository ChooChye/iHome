package com.example.ihome.security

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_security.*
import java.io.*
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.util.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import kotlin.math.floor


class Security : AppCompatActivity() {
    private val FILE_NAME = "example.txt"

    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL
    private val date: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        var actionBar:ActionBar = supportActionBar!!
        actionBar.title = "Security"
        actionBar.setDisplayHomeAsUpEnabled(true)

        checkAlarm()  //check if buzzer is turned on

        security_switch_alarm.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked) {
                alarmController(1)
                now()
            }else{
                alarmController(0)
            }
        })
    }

    private fun alarmController(control:Int){
        var map = mutableMapOf<String,Any>()
        map["buzzer"] = control
        myRef.updateChildren(map) //add into Firebase
    }

    private fun checkAlarm(){
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Log.d("TestingSecurity_READ", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val map: Map<String, Object> = p0.getValue() as Map<String, Object>
                    //Log.d("TestingSecurity_Buzz", "Value is: " + map["buzzer"]);
                    if(map["buzzer"].toString() == 1.toString()){
                        //if buzzer == 1 turn on switch
                        security_switch_alarm.isChecked = true
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun now(){
        var currentTime:LocalDateTime = LocalDateTime.now();
        var fos: FileOutputStream ? = null
        val now = currentTime.toString()
        try{
            fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            fos.write(now.toByteArray())
            Log.d("TestingSecurity", "${now.toByteArray()}")
        }catch (e:FileNotFoundException){
            Log.d("TestingSecurity", "${e.printStackTrace()}")
        }catch (e:IOException){
            Log.d("TestingSecurity", "${e.printStackTrace()}")
        }finally {
            if(fos != null){
                try{
                    fos.close()
                }catch (e:IOException){
                    Log.d("TestingSecurity", "${e.printStackTrace()}")
                }
            }
        }
    }

    private fun load(){
        var fis:FileInputStream ? = null
        try{
            fis = openFileInput(FILE_NAME)
            var isr: InputStreamReader = InputStreamReader(fis)
            var br: BufferedReader = BufferedReader(isr)
            var sb: StringBuilder = StringBuilder()
            val text:String  = br.readLine()
            while (text != null){
                sb.append(text).append("\n")
            }
            Log.d("TestingSecurity_LOAData", "${sb.toString()}")
        }catch (e:FileNotFoundException){
            Log.d("TestingSecurity_LOAD", "${e.printStackTrace()}")
        }catch (e:IOException){
            Log.d("TestingSecurity_LOAD", "${e.printStackTrace()}")
        }finally {
            if(fis != null){
                try{
                    fis.close()
                }catch (e:IOException){
                    Log.d("TestingSecurity_LOAD", "${e.printStackTrace()}")
                }

            }
        }
    }

    private fun getSensorData(){
        val date = "PI_01_"+date
        var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

        myRefSens.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingSecurity_Sensor", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        Log.d("TestingSecurity_Sensor", data.child("ultra").getValue().toString())
                    }
                    //security_textView_report.text = child.toString()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }

    class reportTime(){
        var date : Date ? = null

        constructor(date: Date?) : this() {
            this.date  = date
        }
    }
}