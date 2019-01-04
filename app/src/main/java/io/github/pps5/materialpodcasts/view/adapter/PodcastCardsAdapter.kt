package io.github.pps5.materialpodcasts.view.adapter

import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.PodcastCardLayoutBinding
import io.github.pps5.materialpodcasts.model.Podcast

class PodcastCardsAdapter(
        private val podcasts: List<Podcast>,
        private val selectedListener: PodcastSelectedListener
) : RecyclerView.Adapter<PodcastCardsAdapter.ViewHolder>() {

    companion object {
        private const val HIGH_RESOLUTION_SIZE = "600x600bb.jpg"
    }

    private val regex = Regex("(\\d{2,3}x\\d{2,3})bb.jpg")
    private lateinit var placeholder: ColorDrawable

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        placeholder = ColorDrawable(ContextCompat.getColor(recyclerView.context, R.color.white))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<PodcastCardLayoutBinding>(
                LayoutInflater.from(parent.context), R.layout.podcast_card_layout, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = podcasts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.selectedListener = selectedListener
        podcasts[position].let {
            holder.binding.podcast = it
            val urls =  arrayOf(it.artworkUrl100, it.artworkUrl60, it.artworkUrl30).filterNotNull()
            getArtwork(urls, holder.binding.artwork)
        }
    }

    private fun getUrlGenerator(urls: List<String>): () -> String? {
        var i = 0
        return { if (i < urls.size) urls[i++] else null }
    }

    private fun getArtwork(urls: List<String>, view: ImageView) {
        if (urls.isEmpty()) {
            return
        }
        val get = { url: String, callback: Callback ->
            Picasso.get().load(url)
                    .placeholder(placeholder)
                    .fit()
                    .centerCrop()
                    .into(view, callback)
        }
        val urlGenerator = getUrlGenerator(urls)
        val highResolutionArtworkUrl = regex.replace(urls.first(), HIGH_RESOLUTION_SIZE)
        get(highResolutionArtworkUrl, object : Callback {
            override fun onSuccess() {}
            override fun onError(e: java.lang.Exception?) {
                urlGenerator()?.let { get(it, this) }
            }
        })
    }

    interface PodcastSelectedListener {
        fun onSelectedPodcast(podcast: Podcast)
    }

    class ViewHolder(val binding: PodcastCardLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}