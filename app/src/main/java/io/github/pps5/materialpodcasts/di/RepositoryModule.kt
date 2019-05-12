package io.github.pps5.materialpodcasts.di

import io.github.pps5.materialpodcasts.repository.DetailRepository
import io.github.pps5.materialpodcasts.repository.MediaRepository
import io.github.pps5.materialpodcasts.repository.SearchRepository
import io.github.pps5.materialpodcasts.repository.SubscriptionRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { SearchRepository() }
    single { DetailRepository() }
    single { SubscriptionRepository() }
    single { MediaRepository() }
}