package com.dimits.simpleweatherapp.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dimits.simpleweatherapp.API
import com.dimits.simpleweatherapp.ui.WeatherActivity
import com.dimits.simpleweatherapp.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel : ViewModel() {

    val response = MutableLiveData<Response>()
    val throwable = MutableLiveData<Throwable>()

    fun getWeather(city: String, key: String) {

        API.apiService.getMainResponse(city, "metric", key).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    response.postValue(it)
                    Log.d(WeatherActivity.TAG, "getWeather: Data Fetched")
                },
                {
                    throwable.postValue(it)
                    Log.d(WeatherActivity.TAG, "getWeather: ${it.message}")
                }
            )


    }
}