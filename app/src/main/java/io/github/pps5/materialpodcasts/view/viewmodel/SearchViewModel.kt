package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.pps5.materialpodcasts.extension.switchMap
import io.github.pps5.materialpodcasts.model.ITunesResponse
import io.github.pps5.materialpodcasts.repository.SearchRepository
import io.github.pps5.materialpodcasts.view.customview.FragmentTopBar.SearchBarListener
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class SearchViewModel : ViewModel(), SearchBarListener, KoinComponent {

    companion object {
        private const val SEARCH_DELAY = 1500L
        private val TAG = SearchViewModel::class.java.simpleName
    }

    private val repository: SearchRepository by inject()
    private val executor = Executors.newScheduledThreadPool(1)
    private var future: Future<*>? = null

    val shouldShowNoResults = MutableLiveData<Boolean>()
    private val query = MutableLiveData<String>()
    val podcasts: LiveData<Resource<ITunesResponse>> = query.switchMap(repository::search)

    fun cancel() = future?.cancel(true)

    private fun setQuery(value: String, immediately: Boolean) {
        shouldShowNoResults.postValue(false)
        cancel()
        if (value != query.value) {
            if (immediately) {
                query.postValue(value)
            } else {
                future = executor.schedule({ query.postValue(value) }, SEARCH_DELAY, TimeUnit.MILLISECONDS)
            }
        }
    }

    override fun onEnterSearchBar(text: String) = setQuery(text, true)
    override fun afterTextChanged(text: String) = setQuery(text, false)
    override fun onDeleteQuery() = setQuery("", true)
}
