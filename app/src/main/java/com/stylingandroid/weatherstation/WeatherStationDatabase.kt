package com.stylingandroid.weatherstation

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.CurrentWeatherDao
import com.stylingandroid.weatherstation.model.WeatherForecast
import com.stylingandroid.weatherstation.model.WeatherForecastDao
import com.stylingandroid.weatherstation.model.WeatherForecastItem
import org.threeten.bp.Instant

@Database(
        entities = [
            CurrentWeather::class,
            WeatherForecast::class,
            WeatherForecastItem::class
        ],
        version = 2,
        exportSchema = false
)
@TypeConverters(WeatherStationDatabase.Converters::class)
abstract class WeatherStationDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun weatherForecastDao(): WeatherForecastDao


    class Converters {
        @TypeConverter
        fun instantFromTimestamp(timestamp: Long): Instant =
                Instant.ofEpochMilli(timestamp)

        @TypeConverter
        fun timestampFromInstant(instant: Instant): Long =
                instant.toEpochMilli()
    }
}
