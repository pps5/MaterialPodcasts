package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.di.CACHE_DB
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SearchRepository : BaseRepository(), KoinComponent {

    private val itunesService: ITunesService by inject()
    private val cacheDatabase: AppDatabase by inject(CACHE_DB)

    // todo: error handling
    fun search(query: String): LiveData<Resource<List<Podcast>>> {
        cancel()
        val result = MutableLiveData<Resource<List<Podcast>>>().also { it.postValue(Resource.loading()) }
        if (query.isNotEmpty()) {
            job = GlobalScope.launch {
                val response = itunesService.search(query = query).await().results.toList()
                cache(response)
                result.postValue(Resource.success(response))
            }
        }
        return result
    }

    private fun cache(podcasts: List<Podcast>) {
        val subscriptionDAO = cacheDatabase.getSubscriptionDAO()
        val podcastDAO = cacheDatabase.getPodcastDAO()
        subscriptionDAO.deleteAll()
        cacheDatabase.withTransaction {
            podcasts.forEach {
                subscriptionDAO.insert(Subscription(it.collectionId))
                podcastDAO.insert(it)
            }
        }
    }

}