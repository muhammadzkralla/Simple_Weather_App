package com.dimits.simpleweatherapp

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dimits.simpleweatherapp.databinding.ActivityMainBinding
import com.dimits.simpleweatherapp.databinding.ActivityWeatherBinding
import retrofit2.Retrofit
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    lateinit var geocoder: Geocoder
    lateinit var address:List<Address>
    val key = "c4c69437c55b7fa9d45b57fa60364157"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val longitude = intent.getDoubleExtra("log",0.0)
        val latitude = intent.getDoubleExtra("lat",0.0)
        geocoder = Geocoder(this, Locale.getDefault())
        address = geocoder.getFromLocation(latitude,longitude,1)
        binding.cityName.text = address.get(0).locality

    }
}