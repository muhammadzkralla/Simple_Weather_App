package com.dimits.simpleweatherapp

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dimits.simpleweatherapp.databinding.ActivityWeatherBinding
import com.dimits.simpleweatherapp.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    lateinit var geocoder: Geocoder
    lateinit var address:List<Address>
    lateinit var compositeDisposable: CompositeDisposable
    private lateinit var dialog: AlertDialog
    val key = "c4c69437c55b7fa9d45b57fa60364157"
    lateinit var city:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar,null)
        /**set Dialog*/
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

        compositeDisposable = CompositeDisposable()

        val longitude = intent.getDoubleExtra("log",0.0)
        val latitude = intent.getDoubleExtra("lat",0.0)

        if (intent.getBooleanExtra("bool", false)) {
            geocoder = Geocoder(this, Locale.getDefault())
            address = geocoder.getFromLocation(latitude, longitude, 1)
            city = address.get(0).locality
            binding.cityName.text = city
        }else {
            city = intent.getStringExtra("city").toString()
            binding.cityName.text = city
        }

        getWeather()

    }

    private fun getWeather() {
        compositeDisposable.add(
            API.apiService.getMainResponse(city,"metric",key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        dialog.dismiss()
                        fillTheData(it)
                        Log.d(TAG, "getWeather: Data Fetched")
                    },
                    {
                        Toast.makeText(this,"An Error happened, please check your connection and enable the GPS"
                            , Toast.LENGTH_LONG).show()
                        Log.d(TAG, "getWeather: ${it.message}")
                        finish()
                    }
                )
        )

    }

    private fun fillTheData(response: Response) {
        binding.timeUpdated.text = "Updated At: ${getDateTime(response.dt.toString())}"
        binding.mainTemp.text = response.main?.temp.toString() + " C°"
        binding.humidity.text = response.main?.humidity.toString() + " %"
        binding.maxTemp.text = response.main?.tempMax.toString() + " C°"
        binding.minTemp.text = response.main?.tempMin.toString() + " C°"
        binding.feelsLike.text = "Feels like: " + response.main?.feelsLike.toString() + " C°"

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm a")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
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

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object{
        const val TAG = "WeatherActivity"
    }

}