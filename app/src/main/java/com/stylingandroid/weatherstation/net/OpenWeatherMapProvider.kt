package com.stylingandroid.weatherstation.net

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.stylingandroid.weatherstation.model.CurrentWeather
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val cacheSize: Long = 10 * 1024 * 1024

class OpenWeatherMapProvider(
        context: Context,
        private val appId: String,
        okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .cache(Cache(context.cacheDir, cacheSize))
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build(),
        converterFactory: Converter.Factory = MoshiConverterFactory.create(
                Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
        ),
        retrofit: Retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(converterFactory)
                .build(),
        private val service: OpenWeatherMap = retrofit.create(OpenWeatherMap::class.java),
        private val calls: MutableList<Call<Current>> = mutableListOf()

) : CurrentWeatherProvider {

    override fun request(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit) {
        calls += service.currentWeather(latitude, longitude, appId).apply {
            enqueue(CallbackWrapper(callback))
        }
    }

    override fun cancel() {
        calls.forEach { it.cancel() }
        calls.clear()
    }

    private inner class CallbackWrapper(private val callback: (CurrentWeather) -> Unit) : Callback<Current> {
        override fun onFailure(call: Call<Current>?, t: Throwable?) {
            println("Failure: $t")
            calls.remove(call)
        }

        override fun onResponse(call: Call<Current>?, response: Response<Current>?) {
            calls.remove(call)
            println("Response: $response")
            response?.body()?.also {
                callback(it.currentWeather)
            }
        }
    }
}
