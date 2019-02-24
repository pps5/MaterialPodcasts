package io.github.pps5.materialpodcasts

import android.app.Application
import io.github.pps5.materialpodcasts.di.*
import org.koin.android.ext.android.startKoin

class App : Application() {

    companion object {
        private val modules = listOf(
                httpModule,
                repositoryModule,
                mainModule,
                drawableModule,
                dbModule
        )
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules = modules)
    }
}