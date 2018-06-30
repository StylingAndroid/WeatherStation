package com.stylingandroid.weatherstation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(
        indices = [
            Index(value = ["retrievalLatitude", "retrievalLongitude"]),
            Index(value = ["expiryTime"])
        ]
)
data class WeatherForecast(
        val latitude: Float,
        val longitude: Float,
        val placeName: String,
        override var expiryTime: Instant = Instant.now(),
        override var retrievalLatitude: Float = 0f,
        override var retrievalLongitude: Float = 0f
) : BaseWeather {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}


@Entity(
        foreignKeys = [
            (ForeignKey(
                entity = WeatherForecast::class,
                parentColumns = ["id"],
                childColumns = ["forecastId"],
                onDelete = CASCADE
        ))
        ]
)
data class WeatherForecastItem(
        @ColumnInfo(index = true)
        val forecastId: Long,
        val temperature: Float,
        val temperatureMax: Float,
        val temperatureMin: Float,
        val windSpeed: Float,
        val windDirection: Float,
        val weatherType: String,
        val weatherDescription: String,
        val icon: String,
        val timestamp: Instant
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
