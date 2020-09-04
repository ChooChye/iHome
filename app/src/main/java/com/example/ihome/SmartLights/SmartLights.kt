package com.example.ihome.SmartLights

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.R
import com.example.ihome.security.Reports
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_smart_lights.*
import java.text.SimpleDateFormat
import java.util.*

class SmartLights : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL
    private val date: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())

    private lateinit var sensorData:String
    private lateinit var smartLights_ultra2Listener : ValueEventListener
    private lateinit var smartLights_lightListener : ValueEventListener
    var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_lights)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Smart Lights"
        actionBar.setDisplayHomeAsUpEnabled(true)
        //getSensorData()

       //check if led is turned on

        smartLights_switch_bedroom.setOnClickListener {
            turnOnOffLight_bedroom()
        }
        smartLights_switch_bathroom.setOnClickListener {
            turnOnOffLight_bathroom()
        }
        smartLights_switch_livingroom.setOnClickListener {
            turnOnOffLight_livingroom()

        }
    }

    private fun getUltra2SensorData(){
        smartLights_ultra2Listener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingRoom_Sensor", "${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("ultra2").getValue().toString()
                        if(sensorData < 10.toString()){
                            Log.d("TestingRoom_Sensor", "Intruder")
                            smartLights_textView_YouAreIn.visibility = View.VISIBLE
                            smartLights_textView_sensorRoom.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(smartLights_ultra2Listener)
    }

     @SuppressLint("LongLogTag")
     fun ultra2onDestroy() {
        Log.d("smartLights_ulra2OnDestroy", "ultra2 destroy")
        myRefSens.removeEventListener(smartLights_ultra2Listener)
        super.onDestroy()
    }

    private fun getLightSensorData(){
        smartLights_lightListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingLight_Sensor", "${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("light").getValue().toString()

                        smartLights_textView_brightnessBathroom.text = sensorData.toString()
                        smartLights_textView_brightnessBedroom.text = sensorData.toString()
                        smartLights_textView_brightnessLivingroom.text = sensorData.toString()

                        /*
                        if(sensorData < 10.toString()){
                            Log.d("TestingLight_Sensor", "Intruder")
                            smartLights_textView_YouAreIn.text = "You are IN"
                            smartLights_textView_sensorRoom.visibility = View.VISIBLE
                        }

                         */
                    }
                }
            }
        }
        myRefSens.addValueEventListener(smartLights_lightListener)
    }

     @SuppressLint("LongLogTag")
     fun lightonDestroy() {
        Log.d("smartLights_lightOnDestroy", "light destroy")
        myRefSens.removeEventListener(smartLights_lightListener)
        super.onDestroy()
    }

/*

    //get data user enter the room
    private fun getUltra2SensorData(){
        val date = "PI_01_"+date
        var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

        myRefSens.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingRoom_Sensor", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("ultra2").getValue().toString()

                        if(sensorData >= 10.toString()){
                            //notifyUser()
                            smartLights_textView_YouAreIn.text = "You are IN"
                            smartLights_textView_sensorRoom.visibility = View.VISIBLE
                            Log.d("TestingRoom_Sensor", "Intruder")
                        }
                    }
                }
            }
        })
    }

    //get light data
    private fun getLightSensorData(){
        val date = "PI_01_"+date
        var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

        myRefSens.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingLight_Sensor", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("light").getValue().toString()

                        smartLights_textView_brightnessBathroom.text = sensorData.toString()
                        smartLights_textView_brightnessBedroom.text = sensorData.toString()
                        smartLights_textView_brightnessLivingroom.text = sensorData.toString()
                        /*
                        if(sensorData >= 500.toString()){
                            //notifyUser()
                            smartLights_textView_YouAreIn.text="You are IN"
                            Log.d("TestingLight_Sensor", "Intruder")
                        }

                         */
                    }
                }
            }
        })
    }

    */

    private fun turnOnOffLight_bedroom() {
        if (smartLights_switch_bedroom.isChecked) {
            smartLights_textView_brightness.visibility = View.VISIBLE
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_bedroom.visibility= View.VISIBLE

            lightController(1)

            /*
            if (smartLights_button_bedroom1.isClickable){
                lightController(1)
            }else if(smartLights_button_bedroom2.isClickable){
                lightController(2)
            }else if(smartLights_button_bedroom3.isClickable){
                lightController(3)
            }
            */
        } else {
            smartLights_textView_brightness.visibility = View.INVISIBLE
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_bedroom.visibility= View.INVISIBLE

            lightController(0)
        }
    }

    private fun turnOnOffLight_bathroom() {
        if (smartLights_switch_bathroom.isChecked) {
            smartLights_textView_brightness.visibility = View.VISIBLE
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_bathroom.visibility= View.VISIBLE

            lightController(1)

            /*
            if (smartLights_button_bathroom1.isClickable){
                lightController(1)
            }else if(smartLights_button_bathroom2.isClickable){
                lightController(2)
            }else if(smartLights_button_bathroom3.isClickable){
                lightController(3)
            }
            */
        } else {
            smartLights_textView_brightness.visibility = View.INVISIBLE
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_bathroom.visibility= View.INVISIBLE

            lightController(0)
        }
    }

    private fun turnOnOffLight_livingroom() {
        if (smartLights_switch_livingroom.isChecked) {
            smartLights_textView_brightness.visibility = View.VISIBLE
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_livingroom.visibility= View.VISIBLE

            lightController(1)

            /*
            if (smartLights_button_livingroom1.isClickable){
                lightController(1)
            }else if(smartLights_button_livingroom2.isClickable){
                lightController(2)
            }else if(smartLights_button_livingroom3.isClickable){
                lightController(3)
            }
            */

        } else {
            smartLights_textView_brightness.visibility = View.INVISIBLE
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_livingroom.visibility= View.INVISIBLE

            lightController(0)
        }
    }

    private fun lightController(control:Int){
        var map = mutableMapOf<String,Any>()
        map["led"] = control
        myRef.updateChildren(map) //add into Firebase
    }

    /*
    private fun turnOnOffLight(){
        if (smartLights_switch_bedroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_bedroom.visibility= View.VISIBLE
        } else {
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_bedroom.visibility= View.INVISIBLE
        }

        if (smartLights_switch_bedroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_bathroom.visibility= View.VISIBLE
        } else {
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_bathroom.visibility= View.INVISIBLE
        }

        if (smartLights_switch_bedroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_livingroom.visibility= View.VISIBLE
        } else {
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_imageView_livingroom.visibility= View.INVISIBLE
        }

        /*
        if(smartLights_switch_livingroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_textView_roomOnOff.text="**LIVING ROOM ON**"
        }else{
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            smartLights_textView_roomOnOff.text="**LIVING ROOM OFF**"
        }

         */
    }



     */

    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}