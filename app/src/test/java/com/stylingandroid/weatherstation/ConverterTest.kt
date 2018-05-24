package com.stylingandroid.weatherstation

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.endWith
import io.kotlintest.should
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class ConverterTest {
    private val context: Context = mock()
    private val sharedPreferences: SharedPreferences = mock()
    private val converter = Converter(context, sharedPreferences)
    private val floatArgumentCaptor = ArgumentCaptor.forClass(Float::class.java)
    private val intArgumentCaptor = ArgumentCaptor.forClass(Int::class.java)

    @BeforeEach
    fun setup() {
        whenever(context.getString(eq(R.string.wind_speed), any())).thenReturn("5")
        whenever(context.getString(eq(R.string.temperature_celsius), any())).thenReturn("20°C")
        whenever(context.getString(eq(R.string.temperature_fahrenheit), any())).thenReturn("20°F")
        whenever(context.getString(eq(R.string.temperature_kelvin), any())).thenReturn("20K")
    }

    @Nested
    @DisplayName("Given metric units")
    inner class Metric {
        @BeforeEach
        fun setup() {
            whenever(sharedPreferences.getString(eq("Speed"), anyString())).thenReturn("mps")
            whenever(sharedPreferences.getString(eq("Temperature"), anyString())).thenReturn("celsius")
        }

        @Nested
        @DisplayName("When we convert a temperature of 300K")
        inner class Convert300K {
            private lateinit var result: String

            @BeforeEach
            fun setup() {
                result = converter.temperature(300f)
            }

            @Test
            @DisplayName("Then the correct units are used")
            fun correctUnits() {
                result should endWith("°C")
            }

            @Test
            @DisplayName("Then the correct format string is used")
            fun correctFormat() {
                verify(context, atLeastOnce()).getString(eq(R.string.temperature_celsius), any())
                verify(context, never()).getString(eq(R.string.temperature_fahrenheit), any())
                verify(context, never()).getString(eq(R.string.temperature_kelvin), any())
            }

            @Test
            @DisplayName("Then the correct value is used")
            fun correctValue() {
                verify(context).getString(anyInt(), floatArgumentCaptor.capture())
                val expected = 26.850006f
                assertEquals(expected, floatArgumentCaptor.value)
            }
        }

        @Nested
        @DisplayName("When we convert a speed of 20m/s")
        inner class Convert20mps {
            @BeforeEach
            fun setup() {
                converter.speed(20f)
            }

            @Test
            @DisplayName("Then the correct format string is used")
            fun correctFormat() {
                verify(context, atLeastOnce()).getString(eq(R.string.wind_speed), any())
            }

            @Test
            @DisplayName("Then the correct value is used")
            fun correctValue() {
                verify(context).getString(anyInt(), floatArgumentCaptor.capture())
                val expected = 20f
                assertEquals(expected, floatArgumentCaptor.value)
            }
        }
    }

    @Nested
    @DisplayName("Given imperial units")
    inner class Imperial {
        @BeforeEach
        fun setup() {
            whenever(sharedPreferences.getString(eq("Speed"), anyString())).thenReturn("mph")
            whenever(sharedPreferences.getString(eq("Temperature"), anyString())).thenReturn("fahrenheit")
        }

        @Nested
        @DisplayName("When we convert a temperature of 300K")
        inner class Convert300K {
            private lateinit var result: String

            @BeforeEach
            fun setup() {
                result = converter.temperature(300f)
            }

            @Test
            @DisplayName("Then the correct units are used")
            fun correctUnits() {
                result should endWith("°F")
            }


            @Test
            @DisplayName("Then the correct format string is used")
            fun correctFormat() {
                verify(context, never()).getString(eq(R.string.temperature_celsius), any())
                verify(context, atLeastOnce()).getString(eq(R.string.temperature_fahrenheit), any())
                verify(context, never()).getString(eq(R.string.temperature_kelvin), any())
            }

            @Test
            @DisplayName("Then the correct value is used")
            fun correctValue() {
                verify(context).getString(anyInt(), floatArgumentCaptor.capture())
                val expected = 80.33001f
                assertEquals(expected, floatArgumentCaptor.value)
            }
        }

        @Nested
        @DisplayName("When we convert a speed of 20m/s")
        inner class Convert20mps {
            @BeforeEach
            fun setup() {
                converter.speed(20f)
            }

            @Test
            @DisplayName("Then the correct format string is used")
            fun correctFormat() {
                verify(context, atLeastOnce()).getString(eq(R.string.wind_speed), any())
            }

            @Test
            @DisplayName("Then the correct value is used")
            fun correctValue() {
                verify(context).getString(anyInt(), floatArgumentCaptor.capture())
                val expected = 44.738728f
                assertEquals(expected, floatArgumentCaptor.value)
            }
        }
    }

    @Nested
    @DisplayName("Given no units")
    inner class None {
        @BeforeEach
        fun setup() {
            whenever(sharedPreferences.getString(eq("Speed"), anyString())).thenReturn("")
            whenever(sharedPreferences.getString(eq("Temperature"), anyString())).thenReturn("")
        }

        @Nested
        @DisplayName("When we convert a temperature of 300K")
        inner class Convert300K {
            private lateinit var result: String

            @BeforeEach
            fun setup() {
                result = converter.temperature(300f)
            }

            @Test
            @DisplayName("Then the correct units are used")
            fun correctUnits() {
                result should endWith("K")
            }

            @Test
            @DisplayName("Then the correct format string is used")
            fun correctFormat() {
                verify(context, never()).getString(eq(R.string.temperature_celsius), any())
                verify(context, never()).getString(eq(R.string.temperature_fahrenheit), any())
                verify(context, atLeastOnce()).getString(eq(R.string.temperature_kelvin), any())
            }

            @Test
            @DisplayName("Then the correct value is used")
            fun correctValue() {
                verify(context).getString(anyInt(), floatArgumentCaptor.capture())
                val expected = 300f
                assertEquals(expected, floatArgumentCaptor.value)
            }

        }
    }
}
