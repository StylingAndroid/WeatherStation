package com.stylingandroid.weatherstation.model

import org.threeten.bp.LocalDate


interface DailyForecastProvider {

    fun getDailyForecast(forecastId: Long, city: String, date: LocalDate): DailyForecast
}
