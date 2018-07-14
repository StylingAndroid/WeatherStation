package com.stylingandroid.weatherstation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.FiveDayForecast

class DailyForecastAdapter(
        private val converter: Converter,
        private val listener: View.OnClickListener
) : RecyclerView.Adapter<DailyForecastViewHolder>() {

    var items: MutableList<FiveDayForecast.DailyItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder =
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.daily_forecast_item, parent, false).let {
                        DailyForecastViewHolder(converter, it)
                    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        items[position].also { item ->
            holder.bind(item, listener)
        }
    }
}
