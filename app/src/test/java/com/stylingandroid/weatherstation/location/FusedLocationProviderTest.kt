package com.stylingandroid.weatherstation.location

import android.content.Context
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString

class FusedLocationProviderTest {
    private val context: Context = mock()
    private val fusedLocationProviderClient: FusedLocationProviderClient = mock()
    private val callback1: (latitude: Double, longitude: Double) -> Unit = mock()
    private val callback2: (latitude: Double, longitude: Double) -> Unit = mock()
    private val locationProvider: LocationProvider = FusedLocationProvider(context, fusedLocationProviderClient)

    @BeforeEach
    fun setup() {
        whenever(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED)
    }

    @Nested
    @DisplayName("Given missing permissions")
    inner class MissingPermissions {

        @BeforeEach
        fun setup() {
            whenever(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_DENIED)
        }

        @Test
        @DisplayName("When we register for updates Then we don't request updates from the fusedLocationProviderClient")
        fun doNotRequest() {
            locationProvider.requestUpdates(callback1)

            verify(fusedLocationProviderClient, never()).requestLocationUpdates(any(), any(), anyOrNull())
        }
    }

    @Nested
    @DisplayName("Given granted permissions")
    inner class GrantedPermissions {

        @Test
        @Ignore
        @DisplayName("When we register for updates Then we request updates from the fusedLocationProviderClient")
        fun request() {
            locationProvider.requestUpdates(callback1)

            verify(fusedLocationProviderClient, times(1)).requestLocationUpdates(any(), any(), anyOrNull())
        }
    }

    @Nested
    @DisplayName("Given a single subscriber")
    inner class SingleSubscriber {

        @BeforeEach
        fun setup() {
            locationProvider.requestUpdates(callback1)
        }

        @Test
        @DisplayName("When we register a second subscriber Then we only request updates from the fusedLocationProviderClient once")
        fun requestOnce() {
            locationProvider.requestUpdates(callback2)

            verify(fusedLocationProviderClient, times(1)).requestLocationUpdates(any(), any(), anyOrNull())
        }
    }

    @Nested
    @DisplayName("Given a multiple subscribers")
    inner class MultipleSubscribers {

        @BeforeEach
        fun setup() {
            locationProvider.requestUpdates(callback1)
            locationProvider.requestUpdates(callback2)
        }

        @Test
        @DisplayName("When we unregister a single subscriber Then we don't unsubscribe from the fusedLocationProviderClient")
        fun doNotUnsubscribe() {
            locationProvider.cancelUpdates(callback1)

            verify(fusedLocationProviderClient, never()).removeLocationUpdates(any<LocationCallback>())
        }

        @Test
        @DisplayName("When we unregister both subscribers Then we unsubscribe from the fusedLocationProviderClient")
        fun unsubscribe() {
            locationProvider.cancelUpdates(callback1)
            locationProvider.cancelUpdates(callback2)

            verify(fusedLocationProviderClient, times(1)).removeLocationUpdates(any<LocationCallback>())
        }

        @Test
        @DisplayName("When we unregister a single subscriber twice Then we don't unsubscribe from the fusedLocationProviderClient")
        fun unsubscribeTwice() {
            locationProvider.cancelUpdates(callback1)
            locationProvider.cancelUpdates(callback1)

            verify(fusedLocationProviderClient, never()).removeLocationUpdates(any<LocationCallback>())
        }
    }


}
