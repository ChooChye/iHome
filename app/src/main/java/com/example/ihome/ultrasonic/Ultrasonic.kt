package com.example.ihome.ultrasonic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.ultrasonic.*
import java.util.*

class Ultrasonic : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()

    private val mdate: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private val hour: String = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
    private val date = "PI_01_$mdate"
    private var myRefSens = database.getReference(date).child(hour).orderByKey().limitToLast(1)
    private lateinit var sensorData:String
    private lateinit var sensorUltrasonic: ValueEventListener

    private var progressBar: ProgressBar? = null

    val tag = "DebuggingIOT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ultrasonic)
        val actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Water Tank Reserve"
        actionBar.setDisplayHomeAsUpEnabled(true)

        getUltraSensorData()
    }

    private fun getUltraSensorData(){
        progressBar = findViewById<ProgressBar>(R.id.indicator)
        sensorUltrasonic = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "${p0.toException()}")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val child = p0.children
                    for (data in child){
                        sensorData = data.child("ultra2").value.toString()//change the path variable
                        water_value.text= (sensorData.toDouble()).toString()
                        progressBar!!.progress = sensorData.toDouble().toInt()/2
                        percentage.text= (sensorData.toDouble()/2).toString()
                    }
                }
            }
        }
        myRefSens.addValueEventListener(sensorUltrasonic)
    }

    private fun destroyListeners(){
        myRefSens.removeEventListener(sensorUltrasonic)
    }

    override fun onDestroy() {
        Log.d(tag, "ThermometerOnDestroy")
        destroyListeners()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}
