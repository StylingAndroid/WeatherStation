package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.FiveDayForecast
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_current_weather.*
import kotlinx.android.synthetic.main.fragment_current_weather.view.*
import org.threeten.bp.LocalDate
import javax.inject.Inject


class CurrentWeatherFragment : Fragment(), View.OnClickListener {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var converter: Converter

    private lateinit var dailyForecastAdapter: DailyForecastAdapter

    private lateinit var navController: NavController

    private var currentFiveDayForecast: FiveDayForecast? = null
    private var currentWeather: CurrentWeather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        navController = findNavController(this)

        weatherViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WeatherViewModel::class.java)

        weatherViewModel.currentWeather.observe(this, Observer<CurrentWeather> { current ->
            current?.apply {
                bindCurrent(current)
            }
        })

        weatherViewModel.fiveDayForecast.observe(this, Observer<FiveDayForecast> { forecast ->
            forecast?.apply {
                bindForecast(forecast)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_current_weather, container, false).apply {
        forecasts.apply {
            layoutManager = LinearLayoutManager(inflater.context, RecyclerView.VERTICAL, false)
            adapter = dailyForecastAdapter
        }
    }

    override fun onClick(view: View) {
        forecasts.getChildAdapterPosition(view).also { position ->
            showDailyForecast(dailyForecastAdapter.items[position].date)
        }
    }

    private fun showDailyForecast(date: LocalDate) {
        currentFiveDayForecast?.also {
            val direction = CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToDailyForecastFragment(
                            it.city,
                            it.forecastId,
                            date.toEpochDay()
                    )

            navController.navigate(direction)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        converter = Converter(context)
        dailyForecastAdapter = DailyForecastAdapter(converter, this)
    }

    override fun onResume() {
        super.onResume()
        (context as? AppCompatActivity)?.supportActionBar?.apply {
            setHasOptionsMenu(true)
        }
    }

    private fun bindCurrent(current: CurrentWeather) {
        if (all_widgets.visibility != View.VISIBLE && currentWeather == null && currentFiveDayForecast != null)
            TransitionManager.beginDelayedTransition(content_panel)
        currentWeather = current
        all_widgets.visibility = View.VISIBLE
        city.text = current.placeName
        temperature.text = converter.temperature(current.temperature)
        wind_speed.text = converter.speed(current.windSpeed)
        wind_direction.rotation = current.windDirection
        timestamp.text = converter.timeString(current.timestamp)
        summary.text = current.weatherType
        type_image.contentDescription = current.weatherDescription
        type_image.setImageResource(
                resources.getIdentifier(
                        "ic_${current.icon}",
                        "drawable",
                        context?.packageName
                )
        )
    }

    private fun bindForecast(forecast: FiveDayForecast) {
        currentFiveDayForecast = forecast
        dailyForecastAdapter.items.clear()
        dailyForecastAdapter.items.addAll(forecast.days)
        dailyForecastAdapter.notifyDataSetChanged()
    }
}
