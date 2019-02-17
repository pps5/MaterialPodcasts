package io.github.pps5.materialpodcasts.view.adapter

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ListItemTopLevelHeaderBinding
import io.github.pps5.materialpodcasts.databinding.PodcastCardLayoutBinding
import io.github.pps5.materialpodcasts.extension.observe
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.view.listener.PodcastSelectListener
import io.github.pps5.materialpodcasts.view.viewmodel.SubscriptionViewModel
import io.github.pps5.materialpodcasts.vo.Resource

class SubscriptionAdapter(
        viewModel: SubscriptionViewModel,
        lifecycleOwner: LifecycleOwner
) : MultipleTypeAdapter() {

    companion object {
        private const val HEADER_TYPE = 0
        private const val PODCAST_TYPE = 1
    }

    private val podcastSelectListener: PodcastSelectListener
    private val podcasts: MutableList<Podcast> = arrayListOf()

    init {
        podcastSelectListener = viewModel
        viewModel.podcasts.observe(lifecycleOwner) {
            if (it is Resource.Success) {
                podcasts.clear()
                podcasts.addAll(it.value)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            HEADER_TYPE -> HeaderViewHolder(parent.inflate(R.layout.list_item_top_level_header))
            else -> PodcastCardHolder(parent.inflate(R.layout.podcast_card_layout))
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) HEADER_TYPE else PODCAST_TYPE

    override fun getItemCount(): Int = podcasts.size + 1

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) = viewHolder.bind(position)

    private class HeaderViewHolder(private val binding: ListItemTopLevelHeaderBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            (binding.root.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                    ?.isFullSpan = true
            binding.title = R.string.subscribe
        }
    }

    private inner class PodcastCardHolder(private val binding: PodcastCardLayoutBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) = podcasts[position - 1].let {
            binding.podcast = it
            binding.artwork.getArtworkFromNetwork(it.artworkBaseUrl)
            binding.selectListener = podcastSelectListener
        }
    }
}