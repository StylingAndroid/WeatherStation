package com.stylingandroid.weatherstation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.CurrentWeatherRepository
import com.stylingandroid.weatherstation.model.ForecastRepository
import com.stylingandroid.weatherstation.model.WeatherForecast
import com.stylingandroid.weatherstation.model.WeatherRepository
import com.stylingandroid.weatherstation.ui.WeatherViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindCurrentWeatherRepository(currentWeatherRepository: CurrentWeatherRepository):
            WeatherRepository<CurrentWeather>

    @Binds
    abstract fun bindForecastRepository(forecastRepository: ForecastRepository):
            WeatherRepository<WeatherForecast>

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindCurrentWeatherViewModel(viewModel: WeatherViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
