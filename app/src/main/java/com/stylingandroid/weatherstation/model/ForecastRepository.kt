package com.stylingandroid.weatherstation.model

import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.net.Forecast
import com.stylingandroid.weatherstation.net.WeatherProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

class ForecastRepository @Inject constructor(
        locationProvider: LocationProvider,
        private val weatherProvider: WeatherProvider,
        private val weatherForecastDao: WeatherForecastDao,
        distanceChecker: DistanceChecker
) : WeatherRepository<WeatherForecast>(distanceChecker), DailyForecastProvider {

    override val range: Double = 1000.0

    val fiveDayForecast: WeatherLiveData<FiveDayForecast> =
            WeatherLiveData(locationProvider, weatherProvider, ::updateLocation)

    override fun requestWeather(latitude: Double, longitude: Double, func: (WeatherForecast) -> Unit) =
            weatherProvider.requestWeatherForecast(latitude, longitude) { forecast ->
                launch(CommonPool) {
                    func(store(latitude, longitude, forecast))
                }
            }

    private fun store(latitude: Double, longitude: Double, forecast: Forecast): WeatherForecast =
            forecast.weatherForecast.apply {
                retrievalLatitude = latitude.toFloat()
                retrievalLongitude = longitude.toFloat()
                expiryTime = forecast.expiry()
                weatherForecastDao.insert(this).also { rowId ->
                    id = rowId
                    forecast.list.map { it.weatherForecastItem(rowId) }.also {
                        weatherForecastDao.insertWeatherForecastItems(it)
                    }
                }
            }

    private fun Forecast.expiry(): Instant =
            list.sortedBy { it.time }.first().time

    override fun getWeather(latitude: Double, longitude: Double): WeatherForecast =
            weatherForecastDao.getWeatherForecast(latitude, longitude)

    override fun updateWeather(value: WeatherForecast) {
        fiveDayForecast.postValue(fiveDayTransformer(value))
    }

    override fun getAllLocations(): List<LocationTuple> = weatherForecastDao.getAllLocations()

    override fun deleteOutdated(cutoff: Instant) = weatherForecastDao.deleteOutdated(cutoff)

    private fun fiveDayTransformer(value: WeatherForecast): FiveDayForecast? =
            value.id?.let {
                weatherForecastDao.getWeatherForecastItems(it).let { items ->
                    FiveDayForecast(it, value.placeName, value.expiryTime, dailyItems(items))
                }
            }

    private fun dailyItems(items: List<WeatherForecastItem>): List<FiveDayForecast.DailyItem> =
            items.forecastsGroupedByDay()
                    .map(Map.Entry<LocalDate, List<WeatherForecastItem>>::toDailyItem)

    override fun getDailyForecast(forecastId: Long, city: String, date: LocalDate): DailyForecast =
            weatherForecastDao.getWeatherForecastItemsForDateRange(
                    forecastId,
                    date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    date.atTime(23, 59).toInstant(ZoneOffset.UTC).toEpochMilli()
            ).let { items ->
                DailyForecast(date, city, items)
            }
}
