package com.stylingandroid.weatherstation.di

import androidx.lifecycle.LiveData
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.CurrentWeatherRepository
import com.stylingandroid.weatherstation.model.FiveDayForecast
import com.stylingandroid.weatherstation.model.ForecastRepository
import dagger.Module
import dagger.Provides

@Module
class ViewModelProviderModule {

    @Provides
    fun providesCurrentWeather(currentWeatherRepository: CurrentWeatherRepository): LiveData<CurrentWeather> =
            currentWeatherRepository.currentWeather

    @Provides
    fun providesFiveDayForecast(forecastRepository: ForecastRepository): LiveData<FiveDayForecast> =
            forecastRepository.fiveDayForecast

}
