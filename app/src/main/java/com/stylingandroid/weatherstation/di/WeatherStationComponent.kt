package com.stylingandroid.weatherstation.di

import android.app.Application
import com.stylingandroid.weatherstation.WeatherStationApplication
import com.stylingandroid.weatherstation.location.LocationModule
import com.stylingandroid.weatherstation.net.WeatherModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidBuilder::class,
    WeatherStationModule::class,
    LocationModule::class,
    WeatherModule::class,
    ViewModelModule::class
])
interface WeatherStationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): WeatherStationComponent
    }

    fun inject(application: WeatherStationApplication)
}
