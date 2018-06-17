package com.stylingandroid.weatherstation.ui

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.stylingandroid.weatherstation.Injector
import com.stylingandroid.weatherstation.WeatherStationApplication
import com.stylingandroid.weatherstation.currentWeather
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.registerCurrentWeatherFragmentInjector
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.threeten.bp.Instant

@RunWith(AndroidJUnit4::class)
class CurrentWeatherFragmentTest {
    private val injector = Injector<Fragment>()

    @get:Rule
    @Suppress("UNUSED")
    val testRule: ActivityTestRule<MainActivity> = object : ActivityTestRule<MainActivity>(MainActivity::class.java, true, true) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            injector.apply {
                injectApplication<WeatherStationApplication> { fragmentInjector ->
                    fragmentDispatchingAndroidInjector = fragmentInjector
                }
                registerCurrentWeatherFragmentInjector()
            }
        }
    }

    @Rule
    @JvmField
    val permissionRule: TestRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val dummyWeather = CurrentWeather(
            1f,
            2f,
            "London",
            293f,
            10f,
            180f,
            "Clear",
            "Clear",
            "01d",
            Instant.now(),
            Instant.now(),
            1f,
            2f
    )

    @Test
    fun testLocationDisplaysCorrectly() {
        currentWeather {
            weatherChanged(dummyWeather)

            showsLocation(dummyWeather.placeName)
        }
    }
}
