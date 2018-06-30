package com.stylingandroid.weatherstation.di

import android.content.Context
import androidx.room.Room
import com.stylingandroid.weatherstation.WeatherStationDatabase
import com.stylingandroid.weatherstation.model.DistanceChecker
import com.stylingandroid.weatherstation.model.LocationDistanceChecker
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesWeatherStationDatabase(context: Context): WeatherStationDatabase =
            Room.databaseBuilder(context, WeatherStationDatabase::class.java, "WeatherStationDatabase").build()

    @Provides
    @Singleton
    fun providesCurrentWeatherDao(database: WeatherStationDatabase) =
            database.currentWeatherDao()

    @Provides
    @Singleton
    fun providesWeatherForecastDao(database: WeatherStationDatabase) =
            database.weatherForecastDao()

    @Provides
    fun providesDistanceChecker(): DistanceChecker =
            LocationDistanceChecker()
}
