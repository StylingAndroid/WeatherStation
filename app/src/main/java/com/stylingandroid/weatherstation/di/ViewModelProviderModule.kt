package com.stylingandroid.weatherstation.di

import androidx.lifecycle.LiveData
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.CurrentWeatherRepository
import dagger.Module
import dagger.Provides

@Module
class ViewModelProviderModule {

    @Provides
    fun providesCurrentWeather(currentWeatherRepository: CurrentWeatherRepository): LiveData<CurrentWeather> =
            currentWeatherRepository.currentWeather
}
