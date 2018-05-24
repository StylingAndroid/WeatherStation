package com.stylingandroid.weatherstation.net

import com.stylingandroid.weatherstation.model.Current
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OpenWeatherMap {
    
    @GET("/data/2.5/weather")
    @Headers("Cache-Control: private, max-age=600, max-stale=600")
    fun currentWeather(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("appid") appId: String
    ): Call<Current>
}
