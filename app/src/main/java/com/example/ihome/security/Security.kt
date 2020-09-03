package com.example.ihome.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.nfc.Tag
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

    val TAG = "securityService"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        var actionBar:ActionBar = supportActionBar!!
        actionBar.title = "Security"
        actionBar.setDisplayHomeAsUpEnabled(true)

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
    private fun turnOnShield(){
        security_imageView_unshield.visibility = View.INVISIBLE
        security_imageView_shield.visibility = View.VISIBLE
        security_textView_shieldedStat.text = "Your home is secured"
        startSensorService()
    }

    private fun turnOffShield(){
        security_imageView_unshield.visibility = View.VISIBLE
        security_imageView_shield.visibility = View.INVISIBLE
        security_textView_shieldedStat.text = "Click the shield to secure your home"
        stopSensorService()
    }

    private fun startSensorService(){
        val intent = Intent(this, SecurityService::class.java)
        startService(intent)
    }

    private fun stopSensorService(){
        val intent = Intent(this, SecurityService::class.java)
        stopService(intent)
    }

    fun showLog(message: String){
        Log.d(TAG, message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}