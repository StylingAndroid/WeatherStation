package com.stylingandroid.weatherstation.model

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.Instant

abstract class WeatherRepository<T : BaseWeather>(
        private val distanceChecker: DistanceChecker
) {

    protected abstract val range: Double

    protected fun updateLocation(latitude: Double, longitude: Double) {
        launch(CommonPool) {
            deleteOutdated()
            getClosestInRange(latitude, longitude, range)?.also {
                println("${this@WeatherRepository.javaClass.simpleName}: Cache HIT")
                updateWeather(it)
            } ?: run {
                println("${this@WeatherRepository.javaClass.simpleName}: Cache MISS")
                requestWeather(latitude, longitude) {
                    launch(CommonPool) {
                        updateWeather(it)
                    }
                }
            }
        }
    }

    private fun getClosestInRange(latitude: Double, longitude: Double, range: Double): T? {
        return getAllLocations().map {
            distanceChecker.distanceBetween(
                    latitude,
                    longitude,
                    it.retrievalLatitude,
                    it.retrievalLongitude
            ).let { distance ->
                DistanceTuple(it.retrievalLatitude, it.retrievalLongitude, distance)
            }
        }.sortedBy { it.distance }
                .firstOrNull { it.distance < range }
                ?.let {
                    getWeather(it.latitude, it.longitude)
                }
    }

    protected abstract fun getWeather(latitude: Double, longitude: Double): T

    protected abstract fun deleteOutdated(cutoff: Instant = Instant.now())

    protected abstract fun requestWeather(latitude: Double, longitude: Double, func: (T) -> Unit)

    protected abstract fun updateWeather(value: T)

    protected abstract fun getAllLocations(): List<LocationTuple>

    private data class DistanceTuple(val latitude: Double, val longitude: Double, var distance: Double = 0.0)
}
