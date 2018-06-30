package com.stylingandroid.weatherstation

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.FiveDayForecast
import com.stylingandroid.weatherstation.ui.CurrentWeatherFragment
import com.stylingandroid.weatherstation.ui.WeatherViewModel

internal fun Injector<Fragment>.registerCurrentWeatherFragmentInjector() =
        registerInjector<CurrentWeatherFragment> {
            viewModelFactory = TestViewModelFactory()
        }

fun currentWeather(func: CurrentWeatherFragmentRobot.() -> Unit) =
        CurrentWeatherFragmentRobot().apply(func)

class CurrentWeatherFragmentRobot {
    private val liveData = currentWeather

    fun weatherChanged(newWeather: CurrentWeather) = liveData.postValue(newWeather)

    fun showsLocation(location: String) {
        onView(withId(R.id.city)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(location)))
        }
    }
}

private val currentWeather = MutableLiveData<CurrentWeather>()
private val fiveDayForecast = MutableLiveData<FiveDayForecast>()

private class TestViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == WeatherViewModel::class.java) {
            WeatherViewModel(currentWeather, fiveDayForecast) as T
        } else {
            throw Exception("Not recognised")
        }
    }
}

