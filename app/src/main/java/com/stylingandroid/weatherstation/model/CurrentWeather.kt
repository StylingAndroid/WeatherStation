package com.stylingandroid.weatherstation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.threeten.bp.Instant

@Entity(
        primaryKeys = ["retrievalLatitude", "retrievalLongitude"]
)
data class CurrentWeather(
        val latitude: Float,
        val longitude: Float,
        val placeName: String,
        val temperature: Float,
        val windSpeed: Float,
        val windDirection: Float,
        val weatherType: String,
        val weatherDescription: String,
        val icon: String,
        val timestamp: Instant,
        @ColumnInfo(index = true)
        override var expiryTime: Instant,
        override var retrievalLatitude: Float,
        override var retrievalLongitude: Float
) : BaseWeather
