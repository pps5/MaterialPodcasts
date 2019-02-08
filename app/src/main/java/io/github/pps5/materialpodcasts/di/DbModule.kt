package io.github.pps5.materialpodcasts.di

import android.arch.persistence.room.Room
import io.github.pps5.materialpodcasts.data.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val dbModule = module {

    single("DB_FILE_NAME") { "app_database.db" }

    single {
        Room.databaseBuilder(
                androidApplication(),
                AppDatabase::class.java,
                get("DB_FILE_NAME")
        ).build()
    }
}