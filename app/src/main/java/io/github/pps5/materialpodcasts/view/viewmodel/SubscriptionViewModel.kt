package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.pps5.materialpodcasts.extension.map
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.repository.SubscriptionRepository
import io.github.pps5.materialpodcasts.view.listener.PodcastSelectListener
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SubscriptionViewModel : ViewModel(), KoinComponent, PodcastSelectListener {

    private val repository: SubscriptionRepository by inject()

    private val _podcasts = repository.getSubscription()
    val podcasts: LiveData<Resource<List<Podcast>>>
        get() = _podcasts

    val shouldShowNoResults: LiveData<Boolean> = _podcasts.map {
        !(it is Resource.Success && it.value.isNotEmpty())
    }

    private val _selectedPodcast = MutableLiveData<Podcast>()
    val selectedPodcast: LiveData<Podcast>
        get() = _selectedPodcast
    override fun onSelected(podcast: Podcast?) {
        _selectedPodcast.postValue(podcast)
    }
}