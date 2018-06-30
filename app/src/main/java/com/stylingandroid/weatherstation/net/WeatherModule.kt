package com.stylingandroid.weatherstation.net

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.stylingandroid.weatherstation.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class WeatherModule {

    private val cacheSize: Long = 10 * 1024 * 1024

    @Provides
    @Singleton
    fun providesOkHttpClient(context: Context) : OkHttpClient =
            OkHttpClient.Builder()
                    .cache(Cache(context.cacheDir, cacheSize))
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()

    @Provides
    @Singleton
    fun providesConverterFactory(): Converter.Factory =
            MoshiConverterFactory.create(
                    Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build()
            )

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit =
            Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(converterFactory)
                    .build()

    @Provides
    @Singleton
    fun providesOpenWeatherMap(retrofit: Retrofit): OpenWeatherMap =
            retrofit.create(OpenWeatherMap::class.java)

    @Provides
    @Singleton
    fun providesWeatherProvider(service: OpenWeatherMap): WeatherProvider =
            OpenWeatherMapProvider(service, BuildConfig.API_KEY)
}
