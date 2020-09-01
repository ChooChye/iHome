package com.example.ihome.SmartLights

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.ihome.R

class SmartLights : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_lights)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Smart Lights"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}