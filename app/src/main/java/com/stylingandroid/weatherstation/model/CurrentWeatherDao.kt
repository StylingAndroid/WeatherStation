package com.stylingandroid.weatherstation.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.threeten.bp.Instant

@Dao
interface CurrentWeatherDao {

    @Insert
    fun insertCurrentWeather(currentWeather: CurrentWeather): Long

    @Query("SELECT retrievalLatitude, retrievalLongitude FROM CurrentWeather")
    fun getAllLocations(): List<LocationTuple>

    @Query("SELECT * FROM CurrentWeather WHERE retrievalLatitude = :latitude AND retrievalLongitude = :longitude")
    fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather

    @Query("DELETE FROM CurrentWeather WHERE retrievalTime < :cutoff")
    fun deleteOutdated(cutoff: Instant)
}

data class LocationTuple(val retrievalLatitude: Double, val retrievalLongitude: Double)
