package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.di.APP_DB
import io.github.pps5.materialpodcasts.di.CACHE_DB
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SubscriptionRepository : KoinComponent {

    private val database: AppDatabase by inject(APP_DB)
    private val cacheDatabase: AppDatabase by inject(CACHE_DB)

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
            val existsSubscription = database.getSubscriptionDAO().find(collectionId) != null
            if (existsSubscription) {
                result.postValue(Resource.error(IllegalStateException("Already registered")))
                return@launch
            }
            val podcast = cacheDatabase.getPodcastDAO().find(collectionId)
            val channel = cacheDatabase.getChannelDAO().find(collectionId)
            val tracks = cacheDatabase.getTrackDAO().find(collectionId)
            if (podcast == null || channel == null || tracks.isEmpty()) {
                result.postValue(Resource.error(IllegalStateException("Not found podcast")))
            } else {
                resetIds(podcast, channel, tracks)
                insertSubscription(collectionId, podcast, channel, tracks)
                result.postValue(Resource.success(Unit))
            }
        }
        return result
    }

    private fun resetIds(podcast: Podcast, channel: Channel, tracks: List<Track>) {
        podcast.podcastId = 0
        channel.channelId = 0
        tracks.forEach { it.trackId = 0 }
    }

    private fun insertSubscription(
        collectionId: Long,
        podcast: Podcast,
        channel: Channel,
        tracks: List<Track>
    ) {
        channel.setCollectionIdAndTrackNumber(collectionId)
        database.withTransaction {
            getSubscriptionDAO().insert(Subscription(collectionId))
            getPodcastDAO().insert(podcast)
            getChannelDAO().insert(channel)
            getTrackDAO().insertAll(tracks)
        }
    }

}
