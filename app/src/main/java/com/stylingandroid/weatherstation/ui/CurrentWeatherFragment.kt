package com.stylingandroid.weatherstation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.model.FiveDayForecast
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_current_weather.*
import kotlinx.android.synthetic.main.fragment_current_weather.view.*
import javax.inject.Inject


class CurrentWeatherFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var converter: Converter

    private lateinit var adapter: DailyForecastAdapter

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_current_weather, container, false).apply {
        forecasts.apply {
            layoutManager = LinearLayoutManager(inflater.context, RecyclerView.VERTICAL, false)
            adapter = this@CurrentWeatherFragment.adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        converter = Converter(context)
        adapter = DailyForecastAdapter(converter)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        context?.apply {
            if (this is AppCompatActivity) {
                supportActionBar?.apply {
                    title = getString(R.string.current_weather)
                    setHasOptionsMenu(true)
                    setDisplayHomeAsUpEnabled(false)
                }
            }
        }
    }

    private fun bindCurrent(current: CurrentWeather) {
        TransitionManager.beginDelayedTransition(content_panel)
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
        adapter.items.clear()
        adapter.items.addAll(forecast.days)
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_settings -> {
                    fragmentManager?.transaction(allowStateLoss = true) {
                        replace(R.id.activity_main, PreferencesFragment())
                        addToBackStack(PreferencesFragment::class.java.simpleName)
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
