package io.github.pps5.materialpodcasts.view.fragment

import android.app.Activity
import android.arch.lifecycle.Observer
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
import io.github.pps5.materialpodcasts.view.ItemOffsetDecoration
import io.github.pps5.materialpodcasts.view.adapter.PodcastCardsAdapter
import io.github.pps5.materialpodcasts.view.viewmodel.SearchViewModel
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.let {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
            if (activity is FragmentInteractionListener) {
                it.topBar.onClickNavigateUp = (activity as FragmentInteractionListener)::removeSearchFragment
                it.content.addOnScrollListener(binding.topBar.scrollChangeListener)
            }
            it.content.layoutManager = GridLayoutManager(context, 2)
            it.content.addItemDecoration(ItemOffsetDecoration(context!!, R.dimen.card_offset))
        }
        observeSearchState()
        return binding.root
    }

    private fun observePodcastClick() = viewModel.selectedPodcast.observe(this, Observer {

    })

    private fun observeSearchState() = viewModel.podcasts.observe(this, Observer {
        when (it) {
            is Resource.Loading -> { // no-op
            }
            is Resource.Success -> {
                if (it.value.resultCount == 0) {
                    binding.content.adapter = PodcastCardsAdapter(listOf(), viewModel)
                    binding.notFound.visibility = VISIBLE
                } else {
                    binding.content.adapter = PodcastCardsAdapter(it.value.results.toList(), viewModel)
                    binding.notFound.visibility = GONE
                }
            }
            is Resource.Error -> {
                Log.d("dbg", "error: ${it.throwable.message}")
                Toast.makeText(context, R.string.podcast_loading_error, LENGTH_SHORT).show()
            }
        }
    })

    override fun onDestroyView() {
        (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.topBar.onClickNavigateUp = null
        viewModel.cancel()
        super.onDestroyView()
    }

    interface FragmentInteractionListener {
        fun removeSearchFragment()
    }
}