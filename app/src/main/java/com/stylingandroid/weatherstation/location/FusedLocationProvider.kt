package com.stylingandroid.weatherstation.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.location.component1
import androidx.core.location.component2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

internal class FusedLocationProvider(
        private val context: Context,
        private val fusedLocationProvider: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
) : LocationProvider {

    private var isRegistered = false

    private val permissions: Array<out String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val subscribers = mutableListOf<(latitude: Double, longitude: Double) -> Unit>()

    override fun requestUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        subscribers += callback
        if (!isRegistered && permissions.all { context.checkSelfPermission(it) == PERMISSION_GRANTED }) {
            isRegistered = true
            LocationRequest.create().apply {
                fusedLocationProvider.requestLocationUpdates(this, locationCallback, null)
            }
        }
    }

    override fun cancelUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        subscribers -= callback
        if (subscribers.isEmpty()) {
            fusedLocationProvider.removeLocationUpdates(locationCallback)
            isRegistered = false
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.firstOrNull()?.also { location ->
                val (latitude, longitude) = location
                subscribers.forEach { callback ->
                    callback(latitude, longitude)
                }
            }
        }
    }
}
