package com.example.ihome.Thermometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_smart_lights.*
import kotlinx.android.synthetic.main.activity_thermometer.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Thermometer : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    //private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL

    private val mdate: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
    private val date = "PI_01_"+mdate
    private var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)
    private lateinit var sensorData:String
    private lateinit var sensorHumidListener: ValueEventListener
    private lateinit var sensorTempListener: ValueEventListener
    val TAG = "DebuggingIOT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thermometer)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Thermometer"
        actionBar.setDisplayHomeAsUpEnabled(true)

        getHumidSensorData()
        getTempSensorData()
    }

    private fun getHumidSensorData(){
        sensorHumidListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("ultra2").getValue().toString()

                        thermometer_textView_humid_bedroom.text = sensorData.toString()
                        thermometer_textView_humid_bathroom.text = sensorData.toString()
                        thermometer_textView_humid_livingroom.text = sensorData.toString()
                        if(sensorData < 500.toString()){
                            Log.d(TAG, "Humidity = $sensorData")
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(sensorHumidListener)
    }

    private fun getTempSensorData(){
        sensorTempListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DebuggingIOT", "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        //change the path variable
                        sensorData = data.child("tempe").getValue().toString()

                        thermometer_textView_temp_bedroom.text = sensorData.toString()
                        thermometer_textView_temp_bathroom.text = sensorData.toString()
                        thermometer_textView_temp_livingroom.text = sensorData.toString()
                        if(sensorData < 500.toString()){
                            Log.d(TAG, "Temp = $sensorData")
                        }
                    }
                }
            }
        }
        myRefSens.addValueEventListener(sensorTempListener)
    }

    private fun destroyListeners(){
        myRefSens.removeEventListener(sensorHumidListener)
        myRefSens.removeEventListener(sensorTempListener)
    }
    override fun onDestroy() {
        Log.d(TAG, "ThemometerOnDestroy")
        destroyListeners()
        super.onDestroy()
    }
    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}