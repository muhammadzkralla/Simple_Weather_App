package com.dimits.simpleweatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dimits.simpleweatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.fetchLocationBtn.setOnClickListener {
            fetchLocation()
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java).apply {
                putExtra("city", binding.edtCityName.text.toString())
            }
            startActivity(intent)
        }
    }

    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
        task.addOnSuccessListener {
            if (it != null){
                val intent = Intent(this,WeatherActivity::class.java).apply {
                    putExtra("log",it.longitude)
                    putExtra("lat",it.latitude)
                    putExtra("bool", true)

                }
                startActivity(intent)
            }
        }

    }

}