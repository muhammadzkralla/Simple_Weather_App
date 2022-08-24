package com.dimits.simpleweatherapp

import com.dimits.simpleweatherapp.model.WeatherModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("onecall?/lat={lat}&lon={lon}")
    fun getResponse(@Path("lat") lat:String, @Path("lon") lon:String): Call<WeatherModel>
}

object API {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
}