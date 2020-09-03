package com.example.ihome.SmartLights

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_lights)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Smart Lights"
        actionBar.setDisplayHomeAsUpEnabled(true)



       //check if led is turned on

        smartLights_switch_bedroom.setOnClickListener {
            turnOnOffLight()
        }
        smartLights_switch_bathroom.setOnClickListener {
            turnOnOffLight()
        }
        smartLights_switch_livingroom.setOnClickListener {
            turnOnOffLight()
        }
    }

    private fun lightController(control:Int){
        var map = mutableMapOf<String,Any>()
        map["led"] = control
        myRef.updateChildren(map) //add into Firebase
    }


    private fun turnOnOffLight(){
        if(smartLights_switch_bedroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
        }else{
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
        }

        if(smartLights_switch_bathroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
        }else{
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
        }

        if(smartLights_switch_livingroom.isChecked) {
            smartLights_imageView_off.visibility = View.INVISIBLE
            smartLights_imageView_on.visibility = View.VISIBLE
            lightController(1)
        }else{
            smartLights_imageView_off.visibility = View.VISIBLE
            smartLights_imageView_on.visibility = View.INVISIBLE
            lightController(0)
        }
    }
    /*
    private fun turnOnLight(){
        smartLights_switch_bedroom.isChecked = true
        smartLights_imageView_off.visibility = View.VISIBLE
        smartLights_imageView_on.visibility = View.INVISIBLE
    }

    private fun turnOffLight(){
        smartLights_switch_bedroom.isChecked = false
        smartLights_imageView_off.visibility = View.VISIBLE
        smartLights_imageView_on.visibility = View.INVISIBLE
    }
    */

    //get temperature data
    private fun getSensorData(){
        val date = "PI_01_"+date
        var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)

        myRefSens.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Temperature_Sensor", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("tempe").getValue().toString()
                        //Log.d("TestingSecurity_Sensor", sensorData)
                        if(sensorData >= 26.toString()){
                            //notifyUser()
                            Log.d("Temperature_Sensor", "Temperature high, you should turn OFF light")
                        }else  if(sensorData < 26.toString()){
                            //notifyUser()
                            Log.d("Temperature_Sensor", "Temperature low, you should turn ON light")
                        }
                    }

                }
            }
        })
    }


    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}