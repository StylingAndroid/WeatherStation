package com.stylingandroid.weatherstation.net

import com.squareup.moshi.Json
import com.stylingandroid.weatherstation.model.WeatherForecast
import com.stylingandroid.weatherstation.model.WeatherForecastItem
import org.threeten.bp.Instant

data class Forecast(
        @Json(name = "city") val city: City,
        @Json(name = "list") val list: List<ForecastItem>
) {
    val weatherForecast = WeatherForecast(
            city.coordinates.latitude,
            city.coordinates.longitude,
            city.name
    )
}

data class City(
        @Json(name = "id") val id: Long,
        @Json(name = "name") val name: String,
        @Json(name = "coord") val coordinates: Coordinates,
        @Json(name = "country") val country: String
)

data class ForecastItem(
        @Json(name = "dt") val timestamp: Long,
        @Json(name = "weather") val weather: List<Weather>,
        @Json(name = "main") val temperaturePressure: TemperaturePressure,
        @Json(name = "wind") val wind: Wind,
        @Json(name = "clouds") val clouds: Clouds,
        @Json(name = "rain") val rain: Precipitation?,
        @Json(name = "snow") val snow: Precipitation?
) {
    val time: Instant by lazy { Instant.ofEpochSecond(timestamp) }

    fun weatherForecastItem(forecastId: Long) = WeatherForecastItem(
            forecastId,
            temperaturePressure.temperature,
            temperaturePressure.temperatureMax,
            temperaturePressure.temperatureMin,
            wind.speed ?: 0f,
            wind.direction ?: 0f,
            weather[0].main,
            weather[0].description,
            weather[0].icon,
            time
    )
}

data class Clouds(
        @Json(name = "all") val percentage: Int
)

data class Precipitation(
        @Json(name = "3h") val total: Float?
)
