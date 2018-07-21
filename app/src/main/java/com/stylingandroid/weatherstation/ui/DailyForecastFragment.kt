package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.DailyForecast
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_daily_forecast.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class DailyForecastFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var converter: Converter

    private lateinit var threeHourlyForecastAdapter: ThreeHourlyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WeatherViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        converter = Converter(context)
        threeHourlyForecastAdapter = ThreeHourlyForecastAdapter(converter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val args: DailyForecastFragmentArgs? = (savedInstanceState ?: arguments).let { DailyForecastFragmentArgs.fromBundle(it) }
        val forecastId = args?.forecastId
        val city = args?.city
        val date = args?.date?.let { LocalDate.ofEpochDay(it) }

        if (forecastId == null || city == null || date == null)
            throw IllegalArgumentException("Missing either forecastId, city or date")

        return inflater.inflate(R.layout.fragment_daily_forecast, container, false).apply {
            findViewById<RecyclerView>(R.id.three_hourly_forecasts).apply {
                layoutManager = LinearLayoutManager(inflater.context, RecyclerView.VERTICAL, false)
                adapter = threeHourlyForecastAdapter
            }
            loadDailyForecast(forecastId, city, date)
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    private fun loadDailyForecast(forecastId: Long, city: String, date: LocalDate) = async(UI) {
        bind(withContext(CommonPool) {
            weatherViewModel.getDailyForecast(forecastId, city, date)
        })
    }

    private fun bind(forecast: DailyForecast) {
        city.text = forecast.city
        date.text = forecast.date.format(DateTimeFormatter.ofPattern("EEEE"))
        threeHourlyForecastAdapter.apply {
            items.clear()
            items.addAll(forecast.forecasts)
            notifyDataSetChanged()
        }
    }
}
