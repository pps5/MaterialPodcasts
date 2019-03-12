package io.github.pps5.materialpodcasts.di

import android.support.v4.app.FragmentManager
import io.github.pps5.materialpodcasts.view.Navigator
import io.github.pps5.materialpodcasts.view.viewmodel.PodcastDetailViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.SearchViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.SubscriptionViewModel
import io.github.pps5.materialpodcasts.view.viewmodel.TopBarViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

const val ACTIVITY_SCOPE = "activity_scope"
val mainModule = module {
    scope(ACTIVITY_SCOPE) { (fm: FragmentManager, listener: Navigator.InteractionListener) ->
        Navigator(fm, listener)
    }
    viewModel { TopBarViewModel(get()) }
    viewModel { SearchViewModel() }
    viewModel { (collectionId: Long, feedUrl: String, title: String,
                    artistName: String, artworkBaseUrl: String) ->
        PodcastDetailViewModel(collectionId, feedUrl, title, artistName, artworkBaseUrl)
    }
    viewModel { SubscriptionViewModel() }
}