package com.example.ihome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ihome.SmartLights.SmartLights
import com.example.ihome.Thermometer.Thermometer
import com.example.ihome.adapters.MenuAdapter
import com.example.ihome.models.MenuItem
import com.example.ihome.security.Security
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var arrayList:ArrayList<MenuItem> ? = null
    private var gridView: GridView? = null
    private var languageAdapter: MenuAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.main_gridView)
        arrayList = ArrayList()
        arrayList = setDataList()
        languageAdapter = MenuAdapter(applicationContext, arrayList!!)
        gridView?.adapter = languageAdapter
        gridView?.onItemClickListener = this

    }

    private fun setDataList() : ArrayList<MenuItem>{
        var arrayList:ArrayList<MenuItem> = ArrayList()

        arrayList.add(MenuItem(R.drawable.cctv_camera, "Security"))
        arrayList.add(MenuItem(R.drawable.lamp, "Home Lights"))
        arrayList.add(MenuItem(R.drawable.thermometer, "Thermometer"))

        return arrayList
    }

    private fun moduleController(){

        var map = mutableMapOf<String,Any>()
        map["buzzer"] = "1"
        map["camera"] = "1"
        map["lcd"] = "1"
        map["lcdtext"] = "= App ="
        map["led"] = "0"
        map["relay"] = "0"

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("CONTROL")
        myRef.setValue(map) //add into Firebase

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var items:MenuItem = arrayList!!.get(p2)
        when (p2) {
            0 -> {
                var securityIntent = Intent(this, Security::class.java)
                startActivity(securityIntent)
            }
            1 -> {
                var smartLightsIntent = Intent(this, SmartLights::class.java)
                startActivity(smartLightsIntent)
            }
            2 ->{
                var ThemIntent = Intent(this, Thermometer::class.java)
                startActivity(ThemIntent)
            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }
}