package io.github.pps5.materialpodcasts.di

import io.github.pps5.materialpodcasts.view.viewmodel.PodcastDetailViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.SearchViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.SubscriptionViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.TopBarViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val mainModule = module {
    viewModel { TopBarViewModel(get()) }
    viewModel { SearchViewModel() }
    viewModel { (collectionId: Int, feedUrl: String, title: String,
                    artistName: String, artworkBaseUrl: String) ->
        PodcastDetailViewModel(collectionId, feedUrl, title, artistName, artworkBaseUrl)
    }
    viewModel { SubscriptionViewModel() }
}