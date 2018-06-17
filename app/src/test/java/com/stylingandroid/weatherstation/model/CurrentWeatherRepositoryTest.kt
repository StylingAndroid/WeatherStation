package com.stylingandroid.weatherstation.model

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
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
class CurrentWeatherRepositoryTest {
    private val lifecycleOwner: LifecycleOwner = mock()

    private val lifecycleRegistry = LifecycleRegistry(lifecycleOwner)
    private val observer: Observer<CurrentWeather> = mock()
    private val locationProvider: TestLocationProvider = TestLocationProvider()
    private val currentWeatherDao: CurrentWeatherDao = mock()
    private val distanceChecker: DistanceChecker = mock()

    private val currentWeatherProvider: CurrentWeatherProvider =
            spy(TestCurrentWeatherProvider())

    private val currentWeatherRepository: CurrentWeatherRepository = CurrentWeatherRepository(
            locationProvider, currentWeatherProvider, currentWeatherDao, distanceChecker
    )

    @BeforeEach
    fun setup() {
        whenever(lifecycleOwner.lifecycle).thenReturn(lifecycleRegistry)
        whenever(currentWeatherDao.getAllLocations()).thenReturn(emptyList())
        whenever(currentWeatherDao.insertCurrentWeather(any())).thenReturn(1)
        currentWeatherRepository.observe(lifecycleOwner, observer)
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


    @Nested
    @DisplayName("Given a CurrentWeatherRepository with an Empty Database Cache")
    inner class EmptyCacheTest {

        @BeforeEach
        fun setup() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        @Nested
        @DisplayName("When we update the location")
        inner class UpdateLocation {

            @BeforeEach
            fun setup() {
                locationProvider.trigger()
            }

            @DisplayName("Then we make a call to the CurrentWeatherProvider")
            @Test
            fun callProvider() {
                verify(currentWeatherProvider, times(1)).request(any(), any(), any())
            }
        }
    }

    @Nested
    @DisplayName("Given a CurrentWeatherRepository with a Database Cache with a recent entry")
    inner class PopulatedRecentCacheTest {
        private val locationTuple: LocationTuple = mock()
        private val currentWeather: CurrentWeather = mock()

        @BeforeEach
        fun setup() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            whenever(currentWeatherDao.getAllLocations()).thenReturn(listOf(locationTuple))
            whenever(currentWeatherDao.getCurrentWeather(any(), any())).thenReturn(currentWeather)
        }

        @Nested
        @DisplayName("When we update the location close to the cached entry")
        inner class UpdateNearLocation {

            @BeforeEach
            fun setup() {
                whenever(distanceChecker.distanceBetween(any(), any(), any(), any())).thenReturn(1.0)
                locationProvider.trigger(1.0, 1.0)
            }

            @DisplayName("Then we do not make a call to the CurrentWeatherProvider")
            @Test
            fun doNotCallProvider() {
                verify(currentWeatherProvider, never()).request(any(), any(), any())
            }

            @DisplayName("Then we trigger the observer")
            @Test
            fun callObserver() {
                verify(observer, times(1)).onChanged(any())
            }
        }

        @Nested
        @DisplayName("When we update the location far from the cached entry")
        inner class UpdateFarLocation {

            @BeforeEach
            fun setup() {
                whenever(distanceChecker.distanceBetween(any(), any(), any(), any())).thenReturn(100000.0)
                locationProvider.trigger(1.0, 1.0)
            }

            @DisplayName("Then we make a call to the CurrentWeatherProvider")
            @Test
            fun doNotCallProvider() {
                verify(currentWeatherProvider, times(1)).request(any(), any(), any())
            }

            @DisplayName("Then we trigger the observer")
            @Test
            fun callObserver() {
                verify(observer, times(1)).onChanged(any())
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

    fun trigger(latitude: Double = 1.0, longitude: Double = 1.0) {
        action?.invoke(latitude, longitude)
        Thread.sleep(50L)
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
                Instant.now(),
                Instant.now(),
                latitude.toFloat(),
                longitude.toFloat()
        ))
    }

    override fun cancel() {
    }
}
