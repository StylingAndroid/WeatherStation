package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

fun createDailyForecastFragment(forecastId: Long, city: String, date: LocalDate) =
        DailyForecastFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_FORECAST_ID, forecastId)
                putString(EXTRA_CITY, city)
                putSerializable(EXTRA_DATE, date)
            }
        }

private const val EXTRA_FORECAST_ID = "EXTRA_FORECAST_ID"
private const val EXTRA_CITY = "EXTRA_CITY"
private const val EXTRA_DATE = "EXTRA_DATE"

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

        if (context is AppCompatActivity) {
            context.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setTitle(R.string.daily_forecast)
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val forecastId = savedInstanceState?.getLong(EXTRA_FORECAST_ID) ?: arguments?.getLong(EXTRA_FORECAST_ID)
        val city = savedInstanceState?.getString(EXTRA_CITY) ?: arguments?.getString(EXTRA_CITY)
        val date = (savedInstanceState?.getSerializable(EXTRA_DATE)
                ?: arguments?.getSerializable(EXTRA_DATE)) as LocalDate?

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
