package com.stylingandroid.weatherstation.net

import okhttp3.MediaType
import okhttp3.ResponseBody
import java.io.File
import java.net.URL

class OpenWeatherMapResponses {
    val london: ResponseBody =
    ResponseBody.create(MediaType.parse("application/json"), readAsset("london.json"))
    
    private fun readAsset(filename: String): ByteArray =
            javaClass.classLoader.getResource(filename)?.let { url: URL ->
                File(url.toURI()).readBytes()
            } ?: throw RuntimeException("Unable to open $filename")

}
