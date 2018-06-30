package com.stylingandroid.weatherstation.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.threeten.bp.Instant

@Dao
interface WeatherForecastDao {

    @Insert
    fun insert(weather: WeatherForecast): Long

    @Insert
    fun insertWeatherForecastItems(items: List<WeatherForecastItem>)

    @Query("SELECT retrievalLatitude, retrievalLongitude FROM WeatherForecast")
    fun getAllLocations(): List<LocationTuple>

    @Query("SELECT * FROM WeatherForecast WHERE retrievalLatitude = :latitude AND retrievalLongitude = :longitude")
    fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecast

    @Query("SELECT * FROM WeatherForecastItem WHERE forecastId = :forecastId")
    fun getWeatherForecastItems(forecastId: Long): List<WeatherForecastItem>

    @Query("DELETE FROM WeatherForecast WHERE expiryTime < :cutoff")
    fun deleteOutdated(cutoff: Instant)

}
