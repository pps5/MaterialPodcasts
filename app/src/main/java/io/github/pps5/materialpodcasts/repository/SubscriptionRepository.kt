package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.data.FeedsService
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.di.APP_DB
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SubscriptionRepository : KoinComponent {

    private val iTunesService: ITunesService by inject()
    private val feedsService: FeedsService by inject()
    private val database: AppDatabase by inject(APP_DB)

    fun getSubscription(): LiveData<Resource<List<Podcast>>> {
        val result = MutableLiveData<Resource<List<Podcast>>>().also { it.postValue(Resource.loading()) }
        GlobalScope.launch {
            try {
                result.postValue(Resource.success(database.getPodcastDAO().findAll()))
            } catch (e: Exception) {
                e.printStackTrace()
                result.postValue(Resource.error(e))
            }
        }
        return result
    }

    fun addSubscription(collectionId: Long): MutableLiveData<Resource<Unit>> {
        val result = MutableLiveData<Resource<Unit>>().also { it.postValue(Resource.loading()) }
        GlobalScope.launch {
            val subscriptionDAO = database.getSubscriptionDAO()
            if (subscriptionDAO.find(collectionId) == null) {
                val pair = fetchFromNetwork(collectionId)
                if (pair == null || pair.second.tracks.isNullOrEmpty()) {
                    result.postValue(Resource.error(IllegalStateException("Not found podcast")))
                } else {
                    insertSubscription(collectionId, pair.first, pair.second)
                    result.postValue(Resource.success(Unit))
                }
            } else {
                result.postValue(Resource.error(IllegalStateException("Already registered")))
            }
        }
        return result
    }

    private fun insertSubscription(collectionId: Long, podcast: Podcast, channel: Channel) {
        val tracks = channel.tracks!!.map { it.collectionId = collectionId; it }
        with (database) {
            runInTransaction {
                getSubscriptionDAO().insert(Subscription(collectionId))
                getPodcastDAO().insert(podcast)
                getChannelDAO().insert(channel.also { it.collectionId = collectionId })
                getTrackDAO().insertAll(tracks)
            }
        }
    }

    private suspend fun fetchFromNetwork(collectionId: Long): Pair<Podcast, Channel>? {
        val itunesResponse = iTunesService.lookup(collectionId).await()
        val isValidResponse = !itunesResponse.results.getOrNull(0)?.feedUrl.isNullOrEmpty()
        if (isValidResponse) {
            val channel = feedsService.getFeeds(itunesResponse.results[0].feedUrl!!).await()
            return itunesResponse.results[0] to channel
        }
        return null
    }

    fun removeSubscription(collectionId: Long) {
        GlobalScope.launch {
            database.getSubscriptionDAO().delete(collectionId)
        }
    }
}
