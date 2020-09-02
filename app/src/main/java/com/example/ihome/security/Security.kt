package com.example.ihome.security

import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
        security_imageView_unshield.setOnClickListener {
            turnOnShield()
        }
        security_switch_alarm.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked) {
                alarmController(1)
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

    private fun turnOnShield(){
        security_imageView_unshield.visibility = View.INVISIBLE
        security_imageView_shield.visibility = View.VISIBLE
        security_textView_shieldedStat.text = "Your home is secured"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}