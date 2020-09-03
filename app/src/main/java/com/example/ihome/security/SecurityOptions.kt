package com.example.ihome.security

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.ihome.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_security_options.*

class SecurityOptions : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("PI_01_CONTROL") //PI_01_CONTROL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_options)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Security Options"
        actionBar.setDisplayHomeAsUpEnabled(true)
        security_switch_alarm.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked) {
                alarmController(1)
            }else{
                alarmController(0)
            }
        })
    }

    private fun alarmController(control:Int){
        var map = mutableMapOf<String,Any>()
        map["buzzer"] = control
        myRef.updateChildren(map) //add into Firebase
    }
    private fun checkAlarm(){
            myRef.addValueEventListener(object : ValueEventListener {
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
        }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}