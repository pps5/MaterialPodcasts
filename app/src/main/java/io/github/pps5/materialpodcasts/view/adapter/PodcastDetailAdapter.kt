package io.github.pps5.materialpodcasts.view.adapter

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.util.Log
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ListItemAboutBinding
import io.github.pps5.materialpodcasts.databinding.ListItemActionBinding
import io.github.pps5.materialpodcasts.databinding.ListItemEpisodeHeaderBinding
import io.github.pps5.materialpodcasts.databinding.ListItemTrackBinding
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.view.viewmodel.PodcastDetailViewModel
import io.github.pps5.materialpodcasts.vo.Resource
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PodcastDetailAdapter(
    private val viewModel: PodcastDetailViewModel,
    private val trackSelectListener: TrackSelectListener,
    lifecycleOwner: LifecycleOwner
) : MultipleTypeAdapter() {

    companion object {
        private val TAG = PodcastDetailAdapter::class.java.simpleName
        private const val PODCAST_DATA_TYPE = 0
        private const val ACTION_TYPE = 1
        private const val EPISODE_HEADER_TYPE = 2
    }

    private val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
    private val parser = DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss Z")
    private var trackList = listOf<Track>()
    private val channelObserver = Observer<Resource<Channel>> {
        when (it) {
            is Resource.Loading -> Log.d(TAG, "loading channel")
            is Resource.Success -> {
                it.value.description?.let { d -> viewModel.setDescription(d) }
                if (!it.value.tracks.isNullOrEmpty()) {
                    trackList = it.value.tracks!!
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            PODCAST_DATA_TYPE -> PodcastDataViewHolder(parent.inflate(R.layout.list_item_about))
            ACTION_TYPE -> ActionsViewHolder(parent.inflate(R.layout.list_item_action), viewModel)
            EPISODE_HEADER_TYPE -> EpisodeHeaderViewHolder(parent.inflate(R.layout.list_item_episode_header))
            else -> TrackViewHolder(parent.inflate(R.layout.list_item_track))
        }
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = trackList.size + 1

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) = viewHolder.bind(position)

    private inner class TrackViewHolder(val binding: ListItemTrackBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.item = trackList[position - 3]
            binding.pubDate = LocalDate.parse(trackList[position - 3].pubDate, parser).format(formatter)
            binding.trackSelectListener = trackSelectListener
        }
    }

    private inner class PodcastDataViewHolder(val binding: ListItemAboutBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.viewModel = viewModel
        }
    }

    private class ActionsViewHolder(
        private val binding: ListItemActionBinding,
        private val actionClickListener: ActionClickListener
    ) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.eventListener = actionClickListener
        }
    }

    private class EpisodeHeaderViewHolder(binding: ListItemEpisodeHeaderBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {}
    }

    enum class ActionType { SUBSCRIBE, DOWNLOAD, SHARE }
    interface ActionClickListener {
        fun onActionClicked(type: ActionType)
    }

    interface TrackSelectListener {
        fun onSelect(track: Track)
    }
}
