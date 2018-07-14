package com.stylingandroid.weatherstation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.WeatherForecastItem

class ThreeHourlyForecastAdapter(
        private val converter: Converter
) : RecyclerView.Adapter<ThreeHourlyForecastViewHolder>() {

    val items: MutableList<WeatherForecastItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreeHourlyForecastViewHolder =
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.three_hourly_forecast_item, parent, false).let {
                        ThreeHourlyForecastViewHolder(converter, it)
                    }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ThreeHourlyForecastViewHolder, position: Int) =
            holder.bind(items[position])
}
