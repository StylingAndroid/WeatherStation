package com.stylingandroid.weatherstation

import android.app.Application
import androidx.test.InstrumentationRegistry
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import javax.inject.Provider

internal class Injector<T> {
    private val providerMap: MutableMap<Class<out T>, Provider<AndroidInjector.Factory<out T>>> = mutableMapOf()
    private val dispatchingAndroidInjector: DispatchingAndroidInjector<T> = DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(providerMap)

    inline fun <reified A : Application> injectApplication(
            crossinline initBlock: A.(injector: DispatchingAndroidInjector<T>) -> Unit
    ) {
        (InstrumentationRegistry.getTargetContext().applicationContext as? A)?.apply {
            initBlock(dispatchingAndroidInjector)
        }
    }

    inline fun <reified F : T> registerInjector(crossinline initBlock: F.() -> Unit) {
        val injector = AndroidInjector<F> { fragment ->
            fragment.initBlock()
        }
        val factory: AndroidInjector.Factory<out T> = AndroidInjector.Factory<F> { injector }
        providerMap[F::class.java] = Provider { factory }
    }
}
