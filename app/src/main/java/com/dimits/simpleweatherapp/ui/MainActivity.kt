package com.dimits.simpleweatherapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.dimits.simpleweatherapp.R
import com.dimits.simpleweatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar, null)

        /**set Dialog*/
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog = builder.create()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.fetchLocationBtn.setOnClickListener {
            dialog.show()
            fetchLocation()
        }

        binding.searchBtn.setOnClickListener {
            if (!binding.edtCityName.text.isEmpty()) {
                val intent = Intent(this, WeatherActivity::class.java).apply {
                    putExtra("city", binding.edtCityName.text.toString())
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "blank text is not accepted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchLocation() {
        val task =
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)


        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                dialog.dismiss()
                val intent = Intent(this, WeatherActivity::class.java).apply {
                    putExtra("log", it.longitude)
                    putExtra("lat", it.latitude)
                    putExtra("bool", true)

                }
                startActivity(intent)
            } else {
                dialog.dismiss()
                Toast.makeText(
                    this,
                    "This action requires internet and GPS enabled.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

}