package com.stylingandroid.weatherstation.model

import org.threeten.bp.Instant

interface BaseWeather {
    var expiryTime: Instant
    var retrievalLatitude: Float
    var retrievalLongitude: Float
}

