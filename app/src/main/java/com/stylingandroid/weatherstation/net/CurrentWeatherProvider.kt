package com.stylingandroid.weatherstation.net

import com.stylingandroid.weatherstation.model.CurrentWeather

interface CurrentWeatherProvider {
    fun request(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit)
    fun cancel()
}
