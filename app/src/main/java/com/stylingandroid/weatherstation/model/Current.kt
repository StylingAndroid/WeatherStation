package com.stylingandroid.weatherstation.model

import com.squareup.moshi.Json
import org.threeten.bp.Instant

data class Current(
        @Json(name = "coord") val coordinates: Coordinates,
        @Json(name = "weather") val weather: List<Weather>,
        @Json(name = "main") val temperaturePressure: TemperaturePressure,
        @Json(name = "wind") val wind: Wind,
        @Json(name = "clouds") val clouds: Clouds,
        @Json(name = "rain") val rain: Precipitation?,
        @Json(name = "snow") val snow: Precipitation?,
        @Json(name = "dt") val timestamp: Long,
        @Json(name = "sys") val sys: Sys,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String
) {
    val time: Instant by lazy { Instant.ofEpochSecond(timestamp) }
}

data class Sys(
        @Json(name = "country") val country: String,
        @Json(name = "sunrise") val sunrise: Long,
        @Json(name = "sunset") val sunset: Long
)
