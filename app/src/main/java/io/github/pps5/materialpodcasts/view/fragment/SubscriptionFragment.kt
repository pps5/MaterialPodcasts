package io.github.pps5.materialpodcasts.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.FragmentSubscriptionBinding
import io.github.pps5.materialpodcasts.extension.inflateBinding
import io.github.pps5.materialpodcasts.extension.observeNonNull
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.view.ItemOffsetDecoration
import io.github.pps5.materialpodcasts.view.MainActivity
import io.github.pps5.materialpodcasts.view.adapter.SubscriptionAdapter
import io.github.pps5.materialpodcasts.view.listener.PodcastSelectListener
import io.github.pps5.materialpodcasts.view.viewmodel.SubscriptionViewModel
import io.github.pps5.materialpodcasts.vo.Resource
import io.github.pps5.materialpodcasts.vo.Resource.Error
import org.koin.android.ext.android.inject


class SubscriptionFragment : Fragment(), PodcastSelectListener {

    companion object {
        val TAG = SubscriptionFragment::class.java.simpleName
    }

    private val viewModel: SubscriptionViewModel by inject()
    private lateinit var binding: FragmentSubscriptionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.inflateBinding(R.layout.fragment_subscription, container)
        binding.let {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
        }
        binding.content.let {
            it.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            it.addItemDecoration(ItemOffsetDecoration(context!!, R.dimen.card_offset))
            it.adapter = SubscriptionAdapter(viewModel, this)
        }
        viewModel.podcasts.observeNonNull(this, ::onLoadingStateChanged)
        return binding.root
    }

    private fun onLoadingStateChanged(podcasts: Resource<List<Podcast>>) {
        when (podcasts) {
            is Error -> podcasts.throwable.printStackTrace() // TODO: show error message to user
        }
    }

    override fun onSelected(podcast: Podcast) {
        (activity as? MainActivity)?.addDetailFragment(
                PodcastDetailFragment.newInstance(podcast.collectionId, podcast.feedUrl!!,
                        podcast.trackName, podcast.artistName, podcast.artworkBaseUrl)
        )
    }

}