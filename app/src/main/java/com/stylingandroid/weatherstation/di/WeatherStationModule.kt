package com.stylingandroid.weatherstation.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WeatherStationModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application
}
