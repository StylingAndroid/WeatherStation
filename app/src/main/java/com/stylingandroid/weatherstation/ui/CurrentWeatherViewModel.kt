package com.stylingandroid.weatherstation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stylingandroid.weatherstation.model.CurrentWeather
import javax.inject.Inject

class CurrentWeatherViewModel @Inject constructor(val currentWeather: LiveData<CurrentWeather>) : ViewModel()
