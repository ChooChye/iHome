package com.example.ihome.security

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_security.*


class Security : AppCompatActivity() {

    var database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        var actionBar:ActionBar = supportActionBar!!
        actionBar.title = "Security"
        actionBar.setDisplayHomeAsUpEnabled(true)

        //check if buzzer is turned on
        checkAlarm()

        security_switch_alarm.setOnCheckedChangeListener({ _ , isChecked ->
            val message = if (isChecked) {
                alarmController(1)
            }else{
                alarmController(0)
            }
        })
    }

    private fun alarmController(control:Int){

        var map = mutableMapOf<String,Any>()
        map["buzzer"] = control
        val myRef = database.getReference("CONTROL")
        myRef.updateChildren(map) //add into Firebase

    }

    private fun checkAlarm(){
        val myRef = database.getReference("CONTROL")
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("READ", "${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val map: Map<String, Object> = p0.getValue() as Map<String, Object>
                    //Log.d("TestingSecurity", "Value is: " + map["buzzer"]);
                    if(map["buzzer"].toString() == 1.toString()){
                        //if buzzer == 1 turn on switch
                        security_switch_alarm.isChecked = true
                    }
                }
            }

        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}