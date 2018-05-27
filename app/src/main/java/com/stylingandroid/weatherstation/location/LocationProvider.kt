package com.stylingandroid.weatherstation.location

interface LocationProvider {
    fun requestUpdates(callback: (latitude: Double, longitude: Double) -> Unit)
    fun cancelUpdates(callback: (latitude: Double, longitude: Double) -> Unit)
}
