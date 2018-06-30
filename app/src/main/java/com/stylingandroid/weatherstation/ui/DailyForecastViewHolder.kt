package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.FiveDayForecast
import org.threeten.bp.format.DateTimeFormatter

class DailyForecastViewHolder(
        private val converter: Converter,
        private val itemView: View,
        private val context: Context = itemView.context,
        private val resources: Resources = itemView.resources,
        private val temperatureMax: TextView = itemView.findViewById(R.id.temperature_max),
        private val temperatureMin: TextView = itemView.findViewById(R.id.temperature_min),
        private val windSpeed: TextView = itemView.findViewById(R.id.wind_speed),
        private val windDirection: ImageView = itemView.findViewById(R.id.wind_direction),
        private val date: TextView = itemView.findViewById(R.id.date),
        private val type: TextView = itemView.findViewById(R.id.type),
        private val typeIcon: ImageView = itemView.findViewById(R.id.type_image)
) : RecyclerView.ViewHolder(itemView) {

    fun bind(dailyForecast: FiveDayForecast.DailyItem) {
        temperatureMax.text = converter.temperature(dailyForecast.temperatureMax)
        temperatureMin.text = converter.temperature(dailyForecast.temperatureMin)
        windSpeed.text = converter.speed(dailyForecast.windSpeed)
        windDirection.rotation = dailyForecast.windDirection
        date.text = dailyForecast.date.format(DateTimeFormatter.ofPattern("EEEE"))
        type.text = dailyForecast.type
        typeIcon.setImageResource(
                resources.getIdentifier(
                        "ic_${dailyForecast.icon}",
                        "drawable",
                        context.packageName
                )
        )
    }
}
