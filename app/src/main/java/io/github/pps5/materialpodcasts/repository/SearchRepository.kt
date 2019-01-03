package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.model.ITunesResponse
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchRepository(private val itunesService: ITunesService) {

    private var job: Job? = null

    fun search(query: String): LiveData<Resource<ITunesResponse>> {
        cancel()
        val result = MutableLiveData<Resource<ITunesResponse>>().also { it.postValue(Resource.loading()) }
        if (query.isNotEmpty()) {
            job = GlobalScope.launch {
                val response = itunesService.search(query = query).await()
                result.postValue(Resource.success(response))
            }
        }
        return result
    }

    private fun cancel() {
        if (job?.isCancelled == false) {
            job?.cancel()
        }
    }
}