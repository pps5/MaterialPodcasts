package io.github.pps5.materialpodcasts.view.fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.FragmentSearchBinding
import io.github.pps5.materialpodcasts.extension.observe
import io.github.pps5.materialpodcasts.extension.observeNonNull
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.view.ItemOffsetDecoration
import io.github.pps5.materialpodcasts.view.Navigator
import io.github.pps5.materialpodcasts.view.adapter.PodcastCardsAdapter
import io.github.pps5.materialpodcasts.view.viewmodel.SearchViewModel
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    companion object {
        val TAG = SearchFragment::class.java.simpleName
    }

    private val navigator: Navigator by inject()
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.let {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
            it.topBar.onClickNavigateUp = { activity?.onBackPressed() }
            it.content.addOnScrollListener(binding.topBar.scrollChangeListener)
            it.content.layoutManager = GridLayoutManager(context, 2)
            it.content.addItemDecoration(ItemOffsetDecoration(context!!, R.dimen.card_offset))
        }
        viewModel.let {
            it.selectedPodcast.observe(this, ::onClickPodcast)
            it.podcasts.observeNonNull(this, ::onSearchStateChanged)
        }
        return binding.root
    }

    private fun onClickPodcast(podcast: Podcast?) {
        podcast?.let {
            navigator.navigateToPodcastDetail(it.collectionId, it.feedUrl!!, it.trackName, it.artistName, it.artworkBaseUrl)
            viewModel.resetSelection()
        }
    }

    private fun onSearchStateChanged(podcasts: Resource<List<Podcast>>) {
        when (podcasts) {
            is Resource.Loading -> { // no-op
            }
            is Resource.Success -> {
                val (podcastList, visibility) =
                    if (podcasts.value.isEmpty()) listOf<Podcast>() to VISIBLE
                    else podcasts.value.filter { !it.feedUrl.isNullOrEmpty() && it.isFree() } to GONE
                binding.content.adapter = PodcastCardsAdapter(podcastList, viewModel)
                binding.notFound.visibility = visibility
            }
            is Resource.Error -> {
                Log.d(TAG, "error: ${podcasts.throwable.message}")
                Toast.makeText(context, R.string.podcast_loading_error, LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.topBar.onClickNavigateUp = null
        viewModel.cancel()
        viewModel.selectedPodcast.removeObservers(this)
        viewModel.podcasts.removeObservers(this)
        super.onDestroyView()
    }

}