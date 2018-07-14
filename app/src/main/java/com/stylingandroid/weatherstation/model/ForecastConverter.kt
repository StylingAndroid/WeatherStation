package com.stylingandroid.weatherstation.model

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

fun List<WeatherForecastItem>.forecastsGroupedByDay(): Map<LocalDate, List<WeatherForecastItem>> {
    return groupBy { it.timestamp.atZone(ZoneId.systemDefault()).toLocalDate() }
            .filter { it.value.size == 8 }
}

fun Map.Entry<LocalDate, List<WeatherForecastItem>>.toDailyItem(): FiveDayForecast.DailyItem {
    return FiveDayForecast.DailyItem(
            key,
            value.mostCommonWeatherType,
            value.mostCommonWeatherIcon,
            value.averageWindSpeed,
            value.averageWindDirection,
            value.maximumTemperature,
            value.minimumTemperature
    )
}

private val List<WeatherForecastItem>.mostCommonWeatherType: String
    get() {
        return groupBy { it.weatherType }
                .entries
                .sortedByDescending { it.value.size }
                .first()
                .value
                .first()
                .weatherType
    }

private val List<WeatherForecastItem>.mostCommonWeatherIcon: String
    get() {
        return groupBy { it.icon.dropLast(1) }
                .entries
                .sortedByDescending { it.value.size }
                .first()
                .let {
                    "${it.key}d"
                }
    }

private val List<WeatherForecastItem>.averageWindSpeed: Float
    get() = sumByDouble { it.windSpeed.toDouble() }.toFloat() / 8f

private val List<WeatherForecastItem>.averageWindDirection: Float
    get() = (sumByDouble { it.windDirection + 360.0 }.toFloat() / 8f) - 360f

private val List<WeatherForecastItem>.maximumTemperature: Float
    get() = maxBy { it.temperatureMax }?.temperatureMax ?: 0f

private val List<WeatherForecastItem>.minimumTemperature: Float
    get() = maxBy { it.temperatureMax }?.temperatureMax ?: 0f
