package com.example.ihome.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    val SHARED_PREF : String? = null
    val SECURITY_VAL : String? = null

    private var savedPref: Int? = null
    val TAG = "securityService"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        var actionBar:ActionBar = supportActionBar!!
        actionBar.title = "Security"
        actionBar.setDisplayHomeAsUpEnabled(true)
        loadData()
        updateViews()
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun turnOnShield(){
        security_imageView_unshield.visibility = View.INVISIBLE
        security_imageView_shield.visibility = View.VISIBLE
        security_textView_shieldedStat.text = "Your home is secured"
        saveData(1)
        startSensorService()
    }

    private fun turnOffShield(){
        security_imageView_unshield.visibility = View.VISIBLE
        security_imageView_shield.visibility = View.INVISIBLE
        security_textView_shieldedStat.text = "Click the shield to secure your home"
        saveData(0)
        stopSensorService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startSensorService(){
        val intent = Intent(this, SecurityService::class.java)
        startService(intent)
    }

    private fun stopSensorService(){
        val intent = Intent(this, SecurityService::class.java)
        stopService(intent)
    }

    fun saveData(value : Int){
        val sharedPreferences : SharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(SECURITY_VAL, value)
        editor.apply()
    }

    fun loadData(){
        val sharedPreferences : SharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        savedPref = sharedPreferences.getInt(SECURITY_VAL, 0)
        showLog(savedPref.toString())
    }

    fun updateViews(){
        if(savedPref == 0){
            security_imageView_unshield.visibility = View.VISIBLE
            security_imageView_shield.visibility = View.INVISIBLE
        }else{
            security_imageView_unshield.visibility = View.INVISIBLE
            security_imageView_shield.visibility = View.VISIBLE
        }
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