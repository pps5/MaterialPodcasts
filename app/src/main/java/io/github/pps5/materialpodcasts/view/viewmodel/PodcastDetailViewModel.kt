package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.github.pps5.materialpodcasts.extension.map
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.repository.DetailRepository
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PodcastDetailViewModel(
        feedUrl: String,
        val title: String,
        val artistName: String,
        val artworkBaseUrl: String
) : ViewModel(), KoinComponent {

    private val detailRepository: DetailRepository by inject()

    val channel: LiveData<Resource<Channel>>
        get() = _channel
    private val _channel = detailRepository.getDetail(feedUrl)

    val isLoading: LiveData<Boolean> = _channel.map { it is Resource.Loading }

    val description: LiveData<String>
        get() = _description
    private val _description = MutableLiveData<String>().also { it.value = "" }

    fun setDescription(value: String) = _description.postValue(value)
}
