package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchRepository(private val itunesService: ITunesService) : BaseRepository() {

    fun search(query: String): LiveData<Resource<List<Podcast>>> {
        cancel()
        val result = MutableLiveData<Resource<List<Podcast>>>().also { it.postValue(Resource.loading()) }
        if (query.isNotEmpty()) {
            job = GlobalScope.launch {
                val response = itunesService.search(query = query).await()
                result.postValue(Resource.success(response.results.toList()))
            }
        }
        return result
    }

}