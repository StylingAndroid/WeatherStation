package com.stylingandroid.weatherstation.location

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationProvider(
            context: Context,
            fusedLocationProvider: FusedLocationProviderClient
    ): LocationProvider = FusedLocationProvider(context, fusedLocationProvider)
}
