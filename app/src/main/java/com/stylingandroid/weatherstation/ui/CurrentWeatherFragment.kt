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
import androidx.transition.TransitionManager
import com.stylingandroid.weatherstation.R
import com.stylingandroid.weatherstation.location.LocationProvider
import com.stylingandroid.weatherstation.model.CurrentWeather
import com.stylingandroid.weatherstation.net.CurrentWeatherProvider
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_current_weather.*
import javax.inject.Inject


class CurrentWeatherFragment : Fragment() {

    @Inject lateinit var locationProvider: LocationProvider
    @Inject lateinit var currentWeatherProvider: CurrentWeatherProvider

    private lateinit var converter: Converter

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_current_weather, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        converter = Converter(context)
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
            locationProvider.requestUpdates(::retrieveForecast)
        }
    }

    private fun retrieveForecast(latitude: Double, longitude: Double) {
        currentWeatherProvider.request(latitude, longitude, ::bind)
    }

    private fun bind(current: CurrentWeather) {
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

    override fun onPause() {
        locationProvider.cancelUpdates(::retrieveForecast)
        super.onPause()
    }
}
