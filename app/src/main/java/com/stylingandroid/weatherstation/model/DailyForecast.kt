package com.stylingandroid.weatherstation.model

import org.threeten.bp.LocalDate

data class DailyForecast(
        val date: LocalDate,
        val city: String,
        val forecasts: List<WeatherForecastItem>
)
