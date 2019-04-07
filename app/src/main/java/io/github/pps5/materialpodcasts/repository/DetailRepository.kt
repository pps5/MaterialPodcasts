package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.data.FeedsService
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailRepository(
    private val feedsService: FeedsService,
    private val database: AppDatabase
) : BaseRepository() {

    companion object {
        private val TAG = DetailRepository::class.java.simpleName
    }

    fun getDetail(collectionId: Long, feedUrl: String): MutableLiveData<Resource<Channel>> {
        val result = MutableLiveData<Resource<Channel>>().also { it.postValue(Resource.loading()) }
        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.d(TAG, "Error on fetch detail: ${throwable.message}")
            result.postValue(Resource.error(throwable))
        }
        job = GlobalScope.launch(handler) {
            val channel = findChannelAndTracks(collectionId)
            if (channel == null) {
                Log.d(TAG, "cache miss, fetch from network")
                val response = feedsService.getFeeds(feedUrl).await()
                result.postValue(Resource.success(response))
            } else {
                Log.d(TAG, "fetch from db")
                result.postValue(Resource.success(channel))
            }
        }
        return result
    }

    private fun findChannelAndTracks(collectionId: Long): Channel? {
        val channel = database.getChannelDAO().find(collectionId)
        val tracks = database.getTrackDAO().find(collectionId)
        return if (channel != null && tracks.isNotEmpty())
            channel.also { it.tracks = tracks }
        else
            null
    }

}