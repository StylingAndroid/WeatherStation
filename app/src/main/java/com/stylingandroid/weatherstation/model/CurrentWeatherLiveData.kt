package com.stylingandroid.weatherstation.model

import androidx.lifecycle.LiveData
import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.net.CurrentWeatherProvider
import javax.inject.Inject

class CurrentWeatherLiveData @Inject constructor(
        private val locationProvider: LocationProvider,
        private val currentWeatherProvider: CurrentWeatherProvider
) : LiveData<CurrentWeather>() {

    override fun onActive() {
        super.onActive()
        locationProvider.requestUpdates(::updateLocation)
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        currentWeatherProvider.request(latitude, longitude) { current ->
            postValue(current)
        }
    }

    override fun onInactive() {
        currentWeatherProvider.cancel()
        locationProvider.cancelUpdates(::updateLocation)
        super.onInactive()
    }
}
