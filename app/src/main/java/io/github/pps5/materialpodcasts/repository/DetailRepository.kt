package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.FeedsService
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailRepository(private val feedsService: FeedsService) : BaseRepository() {

    fun getDetail(feedUrl: String): MutableLiveData<Resource<Channel>> {
        cancel()
        val result = MutableLiveData<Resource<Channel>>().also { it.postValue(Resource.loading()) }
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            result.postValue(Resource.error(throwable))
        }
        job = GlobalScope.launch(handler) {
            val response = feedsService.getFeeds(feedUrl).await()
            result.postValue(Resource.success(response))
        }
        return result
    }

}