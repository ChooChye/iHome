package com.example.ihome.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ihome.R
import com.example.ihome.Thermometer.Thermometer
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
    private val CHANNEL_ID = "channel_id_security"
    private val noti_ID = 101
    private val date: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())

    private lateinit var sensorData:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        var actionBar:ActionBar = supportActionBar!!
        actionBar.title = "Security"
        actionBar.setDisplayHomeAsUpEnabled(true)
        createNotificationChannel()
        //checkAlarm()  //check if buzzer is turned on
        security_imageView_unshield.setOnClickListener {
            turnOnShield()
        }
        security_imageView_shield.setOnClickListener {
            turnOffShield()
        }
        security_textView_options.setOnClickListener {
            startActivity(Intent(this, SecurityOptions::class.java))
        }
    }
    /*private fun checkAlarm(){
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
    }*/

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
                        sensorData = data.child("ultra").getValue().toString()
                        //Log.d("TestingSecurity_Sensor", sensorData)
                        if(sensorData == 493.toString()){
                            Log.d("TestingSecurity_Sensor", "Matched")
                        }
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
    private fun turnOffShield(){
        security_imageView_unshield.visibility = View.VISIBLE
        security_imageView_shield.visibility = View.INVISIBLE
        security_textView_shieldedStat.text = "Click the shield to secure your home"
    }

    private fun notifyUser(){
        val textTitle = "Security Alert"
        val textContent = "Intruder Detected"
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(noti_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Security Alert"
            val descriptionText = "Alarm has been triggered"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}