package com.example.ihome.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class SecurityService : Service() {

    private val mdate: String = java.text.SimpleDateFormat("yyyyMMdd",java.util.Locale.getDefault()).format(java.util.Date())
    private val hour: String = java.text.SimpleDateFormat("HH", java.util.Locale.getDefault()).format(java.util.Date())

    private val date = "PI_01_"+mdate
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL")
    var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

    private lateinit var sensorData:String
    private lateinit var sensorListener : ValueEventListener
    private val noti_ID = 101
    private val CHANNEL_ID = "channel_id_security"

    //private var reportList : ArrayList<Reports>? = null

    val TAG = "DebuggingIOT"


    override fun onBind(intent: Intent): IBinder ? {
        return null
    }

    override fun onCreate() {
        showLog("OnCreate")
        createNotificationChannel()
        //timeStamp()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showLog("OnStartCommand")
        getSensorData()
        return Service.START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        showLog("DestroyService")
        if(sensorListener != null){
            myRefSens.removeEventListener(sensorListener)
        }
        super.onDestroy()
    }
    fun showLog(message: String){
        Log.d(TAG, message)
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
                            notifyUser()
                            activateBuzzer()
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(sensorListener)
    }

    private fun activateBuzzer(){
        var map = mutableMapOf<String,Any>()
        map["buzzer"] = "1"
        myRef.updateChildren(map) //add into Firebase
    }

    fun timeStamp() : String{
        //val reportTimeStamp: String = java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss",java.util.Locale.getDefault()).format(java.util.Date())
        val milliseconds = System.currentTimeMillis()
        val sdf = SimpleDateFormat("d MMM yyyy HH:mm:ss")
        val resultDate = Date(milliseconds)
        return sdf.format(resultDate)
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


}
