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
import kotlinx.android.synthetic.main.activity_security_options.*
import kotlinx.android.synthetic.main.activity_smart_lights.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SmartLights : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL
    private val mDate: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val date = "PI_01_"+mDate
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())



    private lateinit var sensorData:String
    private lateinit var smartLights_ultra2Listener : ValueEventListener
    private lateinit var smartLights_lightListener : ValueEventListener
    var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)
    val TAG = "DebuggingIOT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_lights)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Smart Lights"
        actionBar.setDisplayHomeAsUpEnabled(true)

      checkLight()
        getUltra2SensorData()


       //check if led is turned on

        smartLights_switch_bedroom.setOnClickListener {
            turnOnOffLight_bedroom()
            getLightSensorData()

        }
        smartLights_switch_bathroom.setOnClickListener {
            turnOnOffLight_bathroom()
            getLightSensorData()
        }
        smartLights_switch_livingroom.setOnClickListener {
            turnOnOffLight_livingroom()
            getLightSensorData()
        }

    }


    private fun checkLight(){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TestingLight_READ", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                try{
                    if(p0.exists()){
                        val map: Map<String, Object> = p0.getValue() as Map<String, Object>
                        if(map["led"].toString() == 1.toString()){
                            //if led == 1 turn on switch

                            smartLights_switch_bedroom.isChecked=true

                        }
                    }
                }catch (e: IOException){
                    Log.d("Error #312 $e", "error")
                }
            }
        })
    }


    private fun getUltra2SensorData(){
        smartLights_ultra2Listener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("ultra2").getValue().toString()
                        if(sensorData <9.toString()){
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



    private fun getLightSensorData(){
        smartLights_lightListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("light").getValue().toString()

                        smartLights_textView_brightnessBedroom.text = sensorData.toDouble().toString()
                        smartLights_textView_brightnessBathroom.text = sensorData+random()
                        smartLights_textView_brightnessLivingroom.text = sensorData+random()
                    }
                }
            }
        }
        myRefSens.addValueEventListener(smartLights_lightListener)
    }

    @SuppressLint("LongLogTag")
    fun ultra2onDestroy() {

        Log.d("smartLights_ulra2OnDestroy", "ultra2 destroy")
        if(smartLights_ultra2Listener != null){
            myRefSens.removeEventListener(smartLights_ultra2Listener!!)
        }
        super.onDestroy()
    }

     @SuppressLint("LongLogTag")
     fun lightonDestroy() {
        Log.d("smartLights_lightOnDestroy", "light destroy")

         if(smartLights_lightListener != null){
             myRefSens.removeEventListener(smartLights_lightListener!!)
         }
         super.onDestroy()

    }

    private fun random(): Double{
        return ((1..2).random()).toDouble()
    }

    private fun turnOnOffLight_bedroom() {
        if (smartLights_switch_bedroom.isChecked) {
            smartLights_textView_brightness.visibility = View.VISIBLE
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            smartLights_imageView_bedroom.visibility= View.VISIBLE

            lightController(1)

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


    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}