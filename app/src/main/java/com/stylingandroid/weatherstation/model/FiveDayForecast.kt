package com.stylingandroid.weatherstation.model

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate


data class FiveDayForecast(
        val forecastId: Long,
        val city: String,
        val expiryTime: Instant,
        val days: List<DailyItem>
) {

    data class DailyItem(
            val date: LocalDate,
            val type: String,
            val icon: String,
            val windSpeed: Float,
            val windDirection: Float,
            val temperatureMax: Float,
            val temperatureMin: Float
    )
}
