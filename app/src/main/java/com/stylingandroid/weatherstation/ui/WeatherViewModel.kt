package com.stylingandroid.weatherstation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.DailyForecastProvider
import com.stylingandroid.weatherstation.model.FiveDayForecast
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
        val currentWeather: LiveData<CurrentWeather>,
        val fiveDayForecast: LiveData<FiveDayForecast>,
        dailyForecastProvider: DailyForecastProvider
) : ViewModel(), DailyForecastProvider by dailyForecastProvider
