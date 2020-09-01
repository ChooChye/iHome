package com.example.ihome.Thermometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.ihome.R

class Thermometer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thermometer)
        var actionBar: ActionBar = supportActionBar!!
        actionBar.title = "Thermometer"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    //Show back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}