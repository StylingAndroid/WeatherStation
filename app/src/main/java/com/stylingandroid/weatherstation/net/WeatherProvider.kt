package com.stylingandroid.weatherstation.net

import com.stylingandroid.weatherstation.model.CurrentWeather

interface WeatherProvider {
    fun requestCurrentWeather(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit)
    fun requestWeatherForecast(latitude: Double, longitude: Double, callback: (Forecast) -> Unit)
    fun cancel()
}
