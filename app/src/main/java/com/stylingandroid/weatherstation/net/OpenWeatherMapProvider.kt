package com.stylingandroid.weatherstation.net

import com.stylingandroid.weatherstation.model.CurrentWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenWeatherMapProvider(
        private val service: OpenWeatherMap,
        private val appId: String,
        private val calls: MutableList<Call<*>> = mutableListOf()
) : WeatherProvider {

    override fun requestCurrentWeather(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit) {
        calls += service.currentWeather(latitude, longitude, appId).apply {
            enqueue(object : Callback<Current> {
                override fun onFailure(call: Call<Current>, t: Throwable?) {
                    println("Failure: $t")
                    calls.remove(call)
                }

                override fun onResponse(call: Call<Current>, response: Response<Current>) {
                    calls.remove(call)
                    println("Response: $response")
                    response.body()?.apply {
                        callback(currentWeather)
                    }
                }
            })
        }
    }

    override fun requestWeatherForecast(latitude: Double, longitude: Double, callback: (Forecast) -> Unit) {
        calls += service.forecast(latitude, longitude, appId).apply {
            enqueue(object : Callback<Forecast> {
                override fun onFailure(call: Call<Forecast>, t: Throwable?) {
                    println("Failure: $t")
                    calls.remove(call)
                }

                override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                    calls.remove(call)
                    println("Response: $response")
                    response.body()?.also {
                        callback(it)
                    }
                }
            })
        }
    }

    override fun cancel() {
        calls.forEach { it.cancel() }
        calls.clear()
    }
}
