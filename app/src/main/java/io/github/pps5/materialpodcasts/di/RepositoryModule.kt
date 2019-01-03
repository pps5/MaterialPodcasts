package io.github.pps5.materialpodcasts.di

import android.provider.SearchRecentSuggestions
import io.github.pps5.materialpodcasts.repository.SearchRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { SearchRepository(get()) }
}