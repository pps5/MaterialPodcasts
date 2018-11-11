package io.github.pps5.materialpodcasts

import android.app.Application
import io.github.pps5.materialpodcasts.di.httpModule
import org.koin.android.ext.android.startKoin

class App: Application() {

    companion object {
        private val modules = listOf(
                httpModule
        )
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules = modules)
    }
}