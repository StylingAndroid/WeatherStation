package com.stylingandroid.weatherstation.di

import com.stylingandroid.weatherstation.ui.CurrentWeatherFragment
import com.stylingandroid.weatherstation.ui.DailyForecastFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidBuilder {

    @ContributesAndroidInjector
    abstract fun bindCurrentWeatherFragment(): CurrentWeatherFragment

    @ContributesAndroidInjector
    abstract fun bindDailyForecastFragment(): DailyForecastFragment
}
