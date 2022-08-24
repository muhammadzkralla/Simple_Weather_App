package com.dimits.simpleweatherapp

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dimits.simpleweatherapp.databinding.ActivityWeatherBinding
import com.dimits.simpleweatherapp.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    lateinit var geocoder: Geocoder
    lateinit var address:List<Address>
    val key = "c4c69437c55b7fa9d45b57fa60364157"
    lateinit var city:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val longitude = intent.getDoubleExtra("log",0.0)
        val latitude = intent.getDoubleExtra("lat",0.0)
        geocoder = Geocoder(this, Locale.getDefault())
        address = geocoder.getFromLocation(latitude,longitude,1)
        city = address.get(0).locality
        binding.cityName.text = city

        getWeather()

    }

    private fun getWeather() {

        API.apiService.getMainResponse(city,"metric",key).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                fillTheData(it)
            },
            {
                Log.d(TAG, "getWeather: $it")
            }
            )
    }

    private fun fillTheData(response: Response) {
        binding.timeUpdated.text = "Updated At: ${getDateTime(response.dt.toString())}"
        binding.mainTemp.text = response.main?.temp.toString()
        binding.maxTemp.text = response.main?.tempMax.toString()
        binding.minTemp.text = response.main?.tempMin.toString()
        binding.humidity.text = response.main?.humidity.toString() + " %"

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

    companion object{
        const val TAG = "WeatherActivity"
    }
}