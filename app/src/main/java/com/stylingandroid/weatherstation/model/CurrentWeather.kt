package com.stylingandroid.weatherstation.model

import org.threeten.bp.Instant

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
        val timestamp: Instant
)
