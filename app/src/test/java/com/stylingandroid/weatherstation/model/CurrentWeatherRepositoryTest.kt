package com.stylingandroid.weatherstation.model

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.stylingandroid.weatherstation.InstantTaskExecutorExtension
import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.net.CurrentWeatherProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.threeten.bp.Instant

@ExtendWith(InstantTaskExecutorExtension::class)
class CurrentWeatherLiveDataTest {
    private val lifecycleOwner: LifecycleOwner = mock()

    private val lifecycleRegistry = LifecycleRegistry(lifecycleOwner)
    private val observer: Observer<CurrentWeather> = mock()
    private val locationProvider: TestLocationProvider = TestLocationProvider()

    private val currentWeatherProvider: CurrentWeatherProvider =
            TestCurrentWeatherProvider()

    private val currentWeatherLiveData: CurrentWeatherRepository =
            CurrentWeatherRepository(locationProvider, currentWeatherProvider)

    @BeforeEach
    fun setup() {
        whenever(lifecycleOwner.lifecycle).thenReturn(lifecycleRegistry)
        currentWeatherLiveData.observe(lifecycleOwner, observer)
    }

    @Nested
    @DisplayName("Given a CurrentWeatherRepository which is not active")
    inner class Inactive {
        @BeforeEach
        fun setup() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        @Nested
        @DisplayName("When the location is updated")
        inner class NeverActive {

            @BeforeEach
            fun setup() {
                locationProvider.trigger()
            }

            @Test
            @DisplayName("Then we do not receive a callback")
            fun noCallback() {
                verify(observer, never()).onChanged(any())
            }
        }
    }

    @Nested
    @DisplayName("Given a CurrentWeatherRepository which is active")
    inner class Active {
        @BeforeEach
        fun setup() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        @Nested
        @DisplayName("When the location is updated")
        inner class NeverActive {

            @BeforeEach
            fun setup() {
                locationProvider.trigger()
            }

            @Test
            @DisplayName("Then we receive a callback")
            fun callback() {
                verify(observer, times(1)).onChanged(any())
            }
        }
    }

    @Nested
    @DisplayName("Given a CurrentWeatherRepository which was active")
    inner class WasActive {
        @BeforeEach
        fun setup() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }

        @Nested
        @DisplayName("When the location is updated")
        inner class NeverActive {

            @BeforeEach
            fun setup() {
                locationProvider.trigger()
            }

            @Test
            @DisplayName("Then we do not receive a callback")
            fun noCallback() {
                verify(observer, never()).onChanged(any())
            }
        }
    }

}

private class TestLocationProvider : LocationProvider {
    private var action: ((Double, Double) -> Unit)? = null

    override fun cancelUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        action = null
    }

    override fun requestUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        action = callback
    }

    fun trigger() {
        action?.invoke(1.0, 1.0)
    }
}

private class TestCurrentWeatherProvider : CurrentWeatherProvider {
    override fun request(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit) {
        callback(CurrentWeather(
                        1f,
                        1f,
                        "London",
                        293f,
                        10f,
                        180f,
                        "Clear",
                        "Clear",
                        "01d",
                        Instant.now()
                ))
    }

    override fun cancel() {
    }
}

