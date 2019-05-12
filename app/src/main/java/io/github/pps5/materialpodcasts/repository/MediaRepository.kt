package io.github.pps5.materialpodcasts.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.github.pps5.materialpodcasts.data.AppDatabase
import io.github.pps5.materialpodcasts.di.APP_DB
import io.github.pps5.materialpodcasts.di.CACHE_DB
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Track
import kotlinx.coroutines.coroutineScope
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class MediaRepository : BaseRepository(), KoinComponent {

    companion object {
        private const val ROOT_ID = "root"
    }

    private val database: AppDatabase by inject(APP_DB)
    private val cache: AppDatabase by inject(CACHE_DB)

    fun getRoot() = ROOT_ID

    suspend fun getTrack(collectionId: Long, trackNumber: Int): Track? = coroutineScope {
        database.getTrackDAO().find(collectionId, trackNumber)?.let { return@coroutineScope it }
        cache.getTrackDAO().find(collectionId, trackNumber)?.let { return@coroutineScope it }
        return@coroutineScope null
    }

    suspend fun getArtworkUri(collectionId: Long) = coroutineScope {
        database.getPodcastDAO().find(collectionId)?.let { return@coroutineScope getMaximumSizeArtwork(it) }
        cache.getPodcastDAO().find(collectionId)?.let { return@coroutineScope getMaximumSizeArtwork(it) }
        return@coroutineScope null
    }

    private fun getMaximumSizeArtwork(podcast: Podcast) =
        arrayOf(podcast.artworkUrl100, podcast.artworkUrl60, podcast.artworkUrl30)
            .filterNotNull()
            .first()

    fun fetchArtwork(url: String, callback: (Bitmap?) -> Unit) {
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let { callback(it) }
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                callback(null)
                e?.printStackTrace()
            }
        }
        Picasso.get().load(url).into(target)
    }
}
