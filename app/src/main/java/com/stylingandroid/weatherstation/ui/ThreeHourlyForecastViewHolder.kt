package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.WeatherForecastItem
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class ThreeHourlyForecastViewHolder(
        private val converter: Converter,
        private val view: View,
        private val context: Context = view.context,
        private val resources: Resources = view.resources,
        private val time: TextView = view.findViewById(R.id.time),
        private val temperatureMax: TextView = view.findViewById(R.id.temperature_max),
        private val temperatureMin: TextView = view.findViewById(R.id.temperature_min),
        private val windSpeed: TextView = view.findViewById(R.id.wind_speed),
        private val windDirection: ImageView = view.findViewById(R.id.wind_direction),
        private val type: TextView = view.findViewById(R.id.type),
        private val typeIcon: ImageView = view.findViewById(R.id.type_image)
) : RecyclerView.ViewHolder(view) {

    fun bind(forecast: WeatherForecastItem) {
        time.text = LocalTime.from(forecast.timestamp.atZone(ZoneOffset.UTC))
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        temperatureMax.text = converter.temperature(forecast.temperatureMax)
        temperatureMin.text = converter.temperature(forecast.temperatureMin)
        windSpeed.text = converter.speed(forecast.windSpeed)
        windDirection.rotation = forecast.windDirection
        type.text = forecast.weatherType
        typeIcon.setImageResource(
                resources.getIdentifier(
                        "ic_${forecast.icon}",
                        "drawable",
                        context.packageName
                )
        )
    }
}
