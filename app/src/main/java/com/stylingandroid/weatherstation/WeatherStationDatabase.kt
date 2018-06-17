package com.stylingandroid.weatherstation

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.CurrentWeatherDao
import org.threeten.bp.Instant

@Database(entities = [CurrentWeather::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherStationDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
}

class Converters {
    @TypeConverter
    fun instantFromTimestamp(timestamp: Long): Instant =
            Instant.ofEpochMilli(timestamp)

    @TypeConverter
    fun timestampFromInstant(instant: Instant): Long =
            instant.toEpochMilli()
}
