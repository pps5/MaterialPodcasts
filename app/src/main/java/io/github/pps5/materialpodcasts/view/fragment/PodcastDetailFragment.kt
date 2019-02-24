package io.github.pps5.materialpodcasts.view.fragment

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.FragmentDetailBinding
import io.github.pps5.materialpodcasts.extension.args
import io.github.pps5.materialpodcasts.extension.observe
import io.github.pps5.materialpodcasts.extension.observeNonNull
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.view.adapter.PodcastDetailAdapter
import io.github.pps5.materialpodcasts.view.viewmodel.PodcastDetailViewModel
import io.github.pps5.materialpodcasts.vo.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PodcastDetailFragment : Fragment() {

    companion object {
        val TAG: String = PodcastDetailFragment::class.java.simpleName
        private const val ARGS_KEY_COLLECTION_ID = "collection_id"
        private const val ARGS_KEY_FEED_URL = "feed_url"
        private const val ARGS_KEY_ARTIST_NAME = "artist_name"
        private const val ARGS_KEY_TITLE = "title"
        private const val ARGS_KEY_ARTWORK_URL = "artwork_url"

        fun newInstance(collectionId: Int, feedUrl: String, title: String, artistName: String, artworkUrl: String) =
                PodcastDetailFragment().also {
                    it.arguments = Bundle().also { b ->
                        b.putInt(ARGS_KEY_COLLECTION_ID, collectionId)
                        b.putString(ARGS_KEY_FEED_URL, feedUrl)
                        b.putString(ARGS_KEY_TITLE, title)
                        b.putString(ARGS_KEY_ARTIST_NAME, artistName)
                        b.putString(ARGS_KEY_ARTWORK_URL, artworkUrl)
                    }
                }
    }

    private val collectionId: Int by args(ARGS_KEY_COLLECTION_ID)
    private val feedUrl: String by args(ARGS_KEY_FEED_URL)
    private val title: String by args(ARGS_KEY_TITLE)
    private val artistName: String by args(ARGS_KEY_ARTIST_NAME)
    private val artworkUrl: String by args(ARGS_KEY_ARTWORK_URL)
    private val viewModel: PodcastDetailViewModel by viewModel {
        parametersOf(collectionId, feedUrl, title, artistName, artworkUrl)
    }
    private lateinit var binding: FragmentDetailBinding
    private var listener: SearchFragment.FragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.let {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
            it.topBar.onClickNavigateUp = { listener?.onClickNavigateUp() }
        }
        setUpContentContainer()
        viewModel.channel.observe(this, ::onChangeLoadingState)
        viewModel.actionType.observeNonNull(this, ::onClickShare)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SearchFragment.FragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setUpContentContainer() =
            binding.contentContainer.let {
                it.addOnScrollListener(binding.topBar.scrollChangeListener)
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = PodcastDetailAdapter(viewModel, this)
            }

    private fun onChangeLoadingState(state: Resource<Channel>?): Unit =
            if (state is Resource.Loading) binding.progressBar.progressiveStart()
            else binding.progressBar.progressiveStop()

    private fun onClickShare(actionType: PodcastDetailAdapter.ActionType) {
        if (actionType == PodcastDetailAdapter.ActionType.SHARE) {
            val intent = Intent(ACTION_SEND).also {
                it.type = "text/plain"
                it.putExtra(EXTRA_TEXT, "$title - $artistName")
            }
            startActivity(intent)
        }
    }
}