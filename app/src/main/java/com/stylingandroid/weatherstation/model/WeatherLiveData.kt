package com.stylingandroid.weatherstation.model

import androidx.lifecycle.MutableLiveData
import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.net.WeatherProvider

class WeatherLiveData<T>(
        private val locationProvider: LocationProvider,
        private val weatherProvider: WeatherProvider,
        private val callback: (Double, Double) -> Unit
        ) : MutableLiveData<T>() {

    override fun onActive() {
        super.onActive()
        locationProvider.requestUpdates(callback)
    }

    override fun onInactive() {
        weatherProvider.cancel()
        locationProvider.cancelUpdates(callback)
        super.onInactive()
    }
}
