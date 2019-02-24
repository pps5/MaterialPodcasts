package io.github.pps5.materialpodcasts.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.PodcastCardLayoutBinding
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.view.listener.PodcastSelectListener

class PodcastCardsAdapter(
        podcasts: List<Podcast>,
        private val selectedListener: PodcastSelectListener
) : RecyclerView.Adapter<PodcastCardsAdapter.ViewHolder>() {

    private val podcasts = ArrayList<Podcast>().also { it.addAll(podcasts) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<PodcastCardLayoutBinding>(
                LayoutInflater.from(parent.context), R.layout.podcast_card_layout, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = podcasts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.selectListener = selectedListener
        podcasts[position].let {
            holder.binding.podcast = it
            holder.binding.artwork.getArtworkFromNetwork(it.artworkBaseUrl)
        }
    }

    class ViewHolder(val binding: PodcastCardLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}