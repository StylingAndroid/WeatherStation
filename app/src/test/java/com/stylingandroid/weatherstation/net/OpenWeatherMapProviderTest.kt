package com.stylingandroid.weatherstation.net

import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.stylingandroid.weatherstation.model.CurrentWeather
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val API_KEY = ""

class OpenWeatherMapProviderTest {
    private val context: Context = mock()
    private val mockServer = MockWebServer()
    private val responses = OpenWeatherMapResponses()
    private val calls: MutableList<Call<Current>> = spy(mutableListOf())

    private val converterFactory = MoshiConverterFactory.create(
            Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
    )

    private lateinit var retrofit: Retrofit
    private lateinit var service: OpenWeatherMap
    private lateinit var provider: OpenWeatherMapProvider

    private val consumer = CurrentConsumer()

    @BeforeEach
    fun setup() {
        mockServer.start()
        retrofit = Retrofit.Builder()
                .client(OkHttpClient.Builder().build())
                .baseUrl(mockServer.url(""))
                .addConverterFactory(converterFactory)
                .build()
        service = retrofit.create(OpenWeatherMap::class.java)
        provider = OpenWeatherMapProvider(service, API_KEY, calls)
    }

    @Nested
    @DisplayName("Given a successful transaction")
    inner class Success {

        @BeforeEach
        fun setup() {
            MockResponse()
                    .setResponseCode(200)
                    .setBody(responses.london.string()).also {
                        mockServer.enqueue(it)
                    }
            provider.request(1.0, 1.0, consumer::consumeCurrent)
            consumer.await()
        }

        @Nested
        @DisplayName("When we make a request")
        inner class Request {

            @Test
            @DisplayName("Then we receive a current status object")
            fun notNull() {
                assertNotNull(consumer.current)
            }

            @Test
            @DisplayName("Then the data is correct")
            fun checkData() {
                consumer.current?.apply {
                    assertThat(latitude, equalTo(51.51f))
                    assertThat(longitude, equalTo(-0.13f))
                    assertThat(placeName, equalTo("London"))
                    assertThat(weatherType, equalTo("Drizzle"))
                    assertThat(weatherDescription, equalTo("light intensity drizzle"))
                    assertThat(icon, equalTo("09d"))
                    assertThat(temperature, equalTo(280.32f))
                    assertThat(windSpeed, equalTo(4.1f))
                    assertThat(windDirection, equalTo(80f))
                }
            }
        }
    }

    @Test
    @DisplayName("Given an idle connection Then the list of outstanding calls is empty")
    fun noCalls() {
        assertThat(calls.size, equalTo(0))
    }

    @Nested
    @DisplayName("Given a slow transaction")
    inner class SlowTransaction {
        @BeforeEach
        fun setup() {
            MockResponse()
                    .setResponseCode(200)
                    .setBodyDelay(1, TimeUnit.SECONDS)
                    .setBody(responses.london.string()).also {
                        mockServer.enqueue(it)
                    }
        }

        @Nested
        @DisplayName("When we make a request")
        inner class MakeRequest {
            @BeforeEach
            fun setup() {
                provider.request(1.0, 1.0, consumer::consumeCurrent)
            }

            @Test
            @DisplayName("Then a call is added to the calls list")
            fun addCall() {
                assertThat(calls.size, equalTo(1))
            }
        }

        @Test
        @DisplayName("When we cancel the Provider Then the call is also cancelled")
        fun cancelSlowTransaction() {
            provider.request(1.0, 1.0, consumer::consumeCurrent)
            provider.cancel()

            assertThat(calls.size, equalTo(0))
        }

    }

    @AfterEach
    fun teardown() {
        mockServer.shutdown()
    }

}

private open class CurrentConsumer {
    private val latch = CountDownLatch(1)
    var current: CurrentWeather? = null

    fun consumeCurrent(current: CurrentWeather?) {
        this.current = current
        latch.countDown()
    }

    fun await() {
        latch.await()
    }
}

