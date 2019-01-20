package io.github.pps5.materialpodcasts.view.adapter

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ListItemAboutBinding
import io.github.pps5.materialpodcasts.databinding.ListItemActionBinding
import io.github.pps5.materialpodcasts.databinding.ListItemEpisodeHeaderBinding
import io.github.pps5.materialpodcasts.databinding.ListItemTrackBinding
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Item
import io.github.pps5.materialpodcasts.view.viewmodel.PodcastDetailViewModel
import io.github.pps5.materialpodcasts.vo.Resource

open class BaseViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(position: Int) {}
}

class PodcastDetailAdapter(
        private val viewModel: PodcastDetailViewModel,
        lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        private val TAG = PodcastDetailAdapter::class.java.simpleName
        private const val PODCAST_DATA_TYPE = 0
        private const val ACTION_TYPE = 1
        private const val EPISODE_HEADER_TYPE = 2
    }

    private var trackList = listOf<Item>()
    private val channelObserver = Observer<Resource<Channel>> {
        when (it) {
            is Resource.Loading -> Log.d(TAG, "loading channel")
            is Resource.Success -> {
                it.value.description?.let { d -> viewModel.setDescription(d) }
                if (!it.value.items.isNullOrEmpty()) {
                    trackList = it.value.items!!
                    notifyDataSetChanged()
                }
            }
            is Resource.Error -> {
                Log.d(TAG, "error: ${it.throwable}")
                it.throwable.printStackTrace()
            }
        }
    }

    init {
        viewModel.channel.observe(lifecycleOwner, channelObserver)
    }

    private fun <T : ViewDataBinding> ViewGroup.inflate(layoutId: Int) =
            DataBindingUtil.inflate<T>(LayoutInflater.from(context), layoutId, this, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            PODCAST_DATA_TYPE -> PodcastDataViewHolder(parent.inflate(R.layout.list_item_about))
            ACTION_TYPE -> ActionsViewHolder(parent.inflate(R.layout.list_item_action))
            EPISODE_HEADER_TYPE -> EpisodeHeaderViewHolder(parent.inflate(R.layout.list_item_episode_header))
            else -> TrackViewHolder(parent.inflate(R.layout.list_item_track))
        }
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = trackList.size + 1

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) = viewHolder.bind(position)

    private inner class TrackViewHolder(val binding: ListItemTrackBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.item = trackList[position - 1]
        }
    }

    private inner class PodcastDataViewHolder(val binding: ListItemAboutBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.viewModel = viewModel
        }
    }

    private inner class EpisodeHeaderViewHolder(binding: ListItemEpisodeHeaderBinding) : BaseViewHolder(binding)
    private inner class ActionsViewHolder(binding: ListItemActionBinding) : BaseViewHolder(binding)
}
