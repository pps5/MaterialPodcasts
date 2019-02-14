package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.pps5.materialpodcasts.extension.map
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.repository.DetailRepository
import io.github.pps5.materialpodcasts.repository.SubscriptionRepository
import io.github.pps5.materialpodcasts.view.adapter.PodcastDetailAdapter
import io.github.pps5.materialpodcasts.view.adapter.PodcastDetailAdapter.ActionType
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PodcastDetailViewModel(
        val collectionId: Int,
        feedUrl: String,
        val title: String,
        val artistName: String,
        val artworkBaseUrl: String
) : ViewModel(), KoinComponent, PodcastDetailAdapter.ActionClickListener {

    private val detailRepository: DetailRepository by inject()
    private val subscriptionRepository: SubscriptionRepository by inject()

    val channel: LiveData<Resource<Channel>>
        get() = _channel
    private val _channel = detailRepository.getDetail(feedUrl)

    val description: LiveData<String>
        get() = _description
    private val _description = MutableLiveData<String>().also { it.value = "" }
    fun setDescription(value: String) = _description.postValue(value)

    private val _actionType = MutableLiveData<ActionType>()
    val actionType: LiveData<ActionType> = _actionType.map {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (it) {
            ActionType.SUBSCRIBE -> subscriptionRepository.addSubscription(collectionId)
            ActionType.DOWNLOAD -> {
                // TODO: implement download
            }
        }
        it
    }

    override fun onActionClicked(type: ActionType) = _actionType.postValue(type)
}
