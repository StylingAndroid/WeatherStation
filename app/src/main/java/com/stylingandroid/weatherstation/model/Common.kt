package com.stylingandroid.weatherstation.model

import com.squareup.moshi.Json

data class Coordinates(
        @Json(name = "lat") val latitude: Float,
        @Json(name = "lon") val longitude: Float
)


data class Weather(
        @Json(name = "id") val id: Int,
        @Json(name = "main") val main: String,
        @Json(name = "description") val description: String,
        @Json(name = "icon") val icon: String
)

data class TemperaturePressure(
        @Json(name = "temp") val temperature: Float,
        @Json(name = "pressure") val pressure: Float,
        @Json(name = "humidity") val humidity: Int,
        @Json(name = "temp_min") val minTemperature: Float,
        @Json(name = "temp_max") val maxTemperature: Float,
        @Json(name = "sea_level") val pressureSeaLevel: Float?,
        @Json(name = "grnd_level") val pressureGroundLevel: Float?
)

data class Wind(
        @Json(name = "speed") val speed: Float?,
        @Json(name = "deg") val direction: Float?
)

data class Clouds(@Json(name = "all") val cloudiness: Float)

data class Precipitation(@Json(name = "3h") val volume: Float?)
