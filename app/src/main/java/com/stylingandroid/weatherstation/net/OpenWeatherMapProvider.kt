package com.stylingandroid.weatherstation.net

import com.stylingandroid.weatherstation.model.CurrentWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenWeatherMapProvider(
        private val service: OpenWeatherMap,
        private val appId: String,
        private val calls: MutableList<Call<Current>> = mutableListOf()

) : CurrentWeatherProvider {

    override fun request(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit) {
        calls += service.currentWeather(latitude, longitude, appId).apply {
            enqueue(CallbackWrapper(callback))
        }
    }

    override fun cancel() {
        calls.forEach { it.cancel() }
        calls.clear()
    }

    private inner class CallbackWrapper(private val callback: (CurrentWeather) -> Unit) : Callback<Current> {
        override fun onFailure(call: Call<Current>?, t: Throwable?) {
            println("Failure: $t")
            calls.remove(call)
        }

        override fun onResponse(call: Call<Current>?, response: Response<Current>?) {
            calls.remove(call)
            println("Response: $response")
            response?.body()?.also {
                callback(it.currentWeather)
            }
        }
    }
}
