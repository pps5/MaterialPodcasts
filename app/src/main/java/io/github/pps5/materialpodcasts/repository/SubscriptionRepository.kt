package io.github.pps5.materialpodcasts.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.vo.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.lang.IllegalStateException

class SubscriptionRepository : KoinComponent {

    private val iTunesService: ITunesService by inject()
    private val database: AppDatabase by inject()

    fun getSubscription(): LiveData<Resource<List<Podcast>>> {
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

    fun addSubscription(collectionId: Int): MutableLiveData<Resource<Unit>> {
        val result = MutableLiveData<Resource<Unit>>().also { it.postValue(Resource.loading()) }
        GlobalScope.launch {
            val dao = database.getSubscriptionDAO()
            if (dao.find(collectionId) == null) {
                val response = iTunesService.lookup(collectionId).await()
                if (response.results.isNotEmpty()) {
                    dao.insert(response.results[0])
                    result.postValue(Resource.success(Unit))
                } else {
                    result.postValue(Resource.error(IllegalStateException("Not found podcast")))
                }
            } else {
                result.postValue(Resource.error(IllegalStateException("Already registered")))
            }
        }
        return result
    }


    fun removeSubscription(collectionId: Int) {
        GlobalScope.launch {
            database.getSubscriptionDAO().delete(collectionId)
        }
    }
}
