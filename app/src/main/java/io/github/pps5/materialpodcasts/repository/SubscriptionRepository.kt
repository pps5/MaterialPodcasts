package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.provider.Settings
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SubscriptionRepository : KoinComponent {

    private val database: AppDatabase by inject()

    fun getSubscribingPodcasts(): LiveData<Resource<List<Podcast>>> {
        val result = MutableLiveData<Resource<List<Podcast>>>().also { it.postValue(Resource.loading()) }
        GlobalScope.launch {
            try {
                result.postValue(Resource.success(database.getSubscriptionDAO().findAll()))
            } catch (e: Exception) {
                e.printStackTrace()
                result.postValue(Resource.error(e))
            }
        }
        return result
    }

    fun addSubscription(podcast: Podcast) {
        GlobalScope.launch {
            database.getSubscriptionDAO().insert(podcast)
        }
    }


    fun removeSubscription(collectionId: Int) {
        GlobalScope.launch {
            database.getSubscriptionDAO().delete(collectionId)
        }
    }
}
