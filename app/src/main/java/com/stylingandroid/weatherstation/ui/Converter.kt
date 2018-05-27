package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.stylingandroid.weatherstation.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.Locale

class Converter(
        val context: Context,
        private val sharedPreferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context),
        locale: Locale = Locale.getDefault(),
        private val zoneId: ZoneId = ZoneId.systemDefault()
) {

    private val dateTimeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(locale)
            .withZone(zoneId)

    fun speed(value: Float): String =
            sharedPreferences.getString("Speed", "mph").let { units ->
                when (units) {
                    "mph" -> msToMph(value)
                    else -> value
                }
            }.let { newValue ->
                context.getString(R.string.wind_speed, newValue)
            }

    fun temperature(value: Float): String =
            sharedPreferences.getString("Temperature", "celsius").let { units ->
                when (units) {
                    "celsius" -> kelvinToCelsius(value) to R.string.temperature_celsius
                    "fahrenheit" -> kelvinToFahrenheit(value) to R.string.temperature_fahrenheit
                    else -> value to R.string.temperature_kelvin
                }
            }.let { (newValue, template) ->
                context.getString(template, newValue)
            }

    fun timeString(instant: Instant): String =
            LocalDateTime.ofInstant(instant, zoneId).format(dateTimeFormat)

    private fun kelvinToCelsius(value: Float): Float = value - 273.15f
    private fun kelvinToFahrenheit(value: Float): Float = (9f / 5f * (value - 273.15f)) + 32
    private fun msToMph(value: Float): Float = value * 2.2369362920544f

}
