package com.stylingandroid.weatherstation.model

import androidx.lifecycle.LiveData
import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.net.CurrentWeatherProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import javax.inject.Inject


private val validity: Long = Duration.ofMinutes(10).toMillis()
private const val range: Double = 1000.0

class CurrentWeatherRepository @Inject constructor(
        private val locationProvider: LocationProvider,
        private val currentWeatherProvider: CurrentWeatherProvider,
        private val currentWeatherDao: CurrentWeatherDao,
        private val distanceChecker: DistanceChecker
) : LiveData<CurrentWeather>() {

    override fun onActive() {
        super.onActive()
        locationProvider.requestUpdates(::updateLocation)
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        launch(CommonPool) {
            currentWeatherDao.deleteOutdated(Instant.now().minusMillis(validity))
            getClosestInRange(latitude, longitude)?.also {
                postValue(it)
            } ?: run {
                currentWeatherProvider.request(latitude, longitude) {
                    launch(CommonPool) {
                        it.retrievalLatitude = latitude.toFloat()
                        it.retrievalLongitude = longitude.toFloat()
                        currentWeatherDao.insertCurrentWeather(it)
                        postValue(it)
                    }
                }
            }
        }
    }

    private fun getClosestInRange(latitude: Double, longitude: Double): CurrentWeather? {
        return currentWeatherDao.getAllLocations().map {
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
                    currentWeatherDao.getCurrentWeather(it.latitude, it.longitude)
                }
    }

    override fun onInactive() {
        currentWeatherProvider.cancel()
        locationProvider.cancelUpdates(::updateLocation)
        super.onInactive()
    }
}

private data class DistanceTuple(val latitude: Double, val longitude: Double, var distance: Double = 0.0)
