package com.dimits.simpleweatherapp.ui

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dimits.simpleweatherapp.R
import com.dimits.simpleweatherapp.databinding.ActivityWeatherBinding
import com.dimits.simpleweatherapp.model.Response
import com.dimits.simpleweatherapp.viewModels.CoroutineViewModel
import com.dimits.simpleweatherapp.viewModels.State
import com.dimits.simpleweatherapp.viewModels.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    lateinit var geocoder: Geocoder
    lateinit var address: List<Address>
    private lateinit var dialog: AlertDialog
    val key = "c4c69437c55b7fa9d45b57fa60364157"
    lateinit var city: String
    private val viewModel: WeatherViewModel by viewModels()
    private val coroutineViewModel: CoroutineViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar, null)

        /**set Dialog*/
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

        val longitude = intent.getDoubleExtra("log", 0.0)
        val latitude = intent.getDoubleExtra("lat", 0.0)

        if (intent.getBooleanExtra("bool", false)) {
            geocoder = Geocoder(this, Locale.getDefault())
            address = geocoder.getFromLocation(latitude, longitude, 1)
            city = address[0].locality
            binding.cityName.text = city
        } else {
            city = intent.getStringExtra("city").toString()
            binding.cityName.text = city
        }

        setupWithRxJava(city)

    }

    private fun setupWithRxJava(city: String) {
        viewModel.getWeather(city, key)

        viewModel.response.observe(this) {
            fillTheData(it)
        }

        viewModel.throwable.observe(this) {
            Log.d(TAG, "setup: ${it.message}")
            Toast.makeText(
                this,
                "Cannot fetch data, make sure the GPS and wifi are enabled",
                Toast.LENGTH_LONG
            ).show()
            finish()

        }

    }

    private fun setupWithCoroutines(city: String){
        coroutineViewModel.getWeather(city, key)

        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                coroutineViewModel.uiState.collect {
                    when (it){
                        is State.Success -> fillTheData(it.response)
                        is State.Error -> showToast(it.throwable.message.toString())
                    }

                }
            }
        }
    }

    private fun showToast(message: String) {
        dialog.dismiss()
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
            finish()
    }

    @SuppressLint("SetTextI18n")
    private fun fillTheData(response: Response) {
        Log.d("WeatherActivity", "Data Filled Using : ${Thread.currentThread().name}")
        dialog.dismiss()
        binding.apply {
            timeUpdated.text = "Updated At: ${getDateTime(response.dt.toString())}"
            mainTemp.text = response.main?.temp.toString() + " C°"
            humidity.text = response.main?.humidity.toString() + " %"
            maxTemp.text = response.main?.tempMax.toString() + " C°"
            minTemp.text = response.main?.tempMin.toString() + " C°"
            feelsLike.text = "Feels like: " + response.main?.feelsLike.toString() + " C°"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm a")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.weather_activity_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.refresh -> {
                finish()
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "WeatherActivity"
    }

}