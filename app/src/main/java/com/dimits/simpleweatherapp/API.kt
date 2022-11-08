package com.dimits.simpleweatherapp

import com.dimits.simpleweatherapp.model.Response
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import tech.thdev.network.flowcalladapterfactory.FlowCallAdapterFactory

interface ApiService {
    @GET("weather")
    fun getMainResponse(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("APPID") APPID: String
    )
            : Single<Response>

    @GET("weather")
    fun getMainResponseWithCoroutines(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("APPID") APPID: String
    )
            : Flow<Response>
}

object API {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addCallAdapterFactory(FlowCallAdapterFactory())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
}