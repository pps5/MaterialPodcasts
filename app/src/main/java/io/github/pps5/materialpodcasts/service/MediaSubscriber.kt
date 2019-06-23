package io.github.pps5.materialpodcasts.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.github.pps5.materialpodcasts.extension.applyIf
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.repository.MediaRepository
import io.github.pps5.materialpodcasts.service.MediaSessionCallback.Companion.BUNDLE_KEY_DESCRIPTION
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MediaSubscriber(private val context: Context) : KoinComponent {

    var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
    private val playlist = mutableListOf<MediaBrowserCompat.MediaItem>()
    private var controllerCallback: MediaControllerCompat.Callback? = null

    private val mediaRepository: MediaRepository by inject()

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser!!.sessionToken)
            mediaController!!.registerCallback(object : MediaControllerCompat.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                    controllerCallback?.onMetadataChanged(metadata)
                }

                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    controllerCallback?.onPlaybackStateChanged(state)
                }
            })
        }
    }

    fun connect() {
        mediaBrowser = MediaBrowserCompat(context,
            ComponentName(context, MediaService::class.java), connectionCallback, null)
        mediaBrowser?.connect()
    }

    fun disconnect() = mediaBrowser?.disconnect()

    fun pause() = mediaController?.transportControls?.pause()
    fun seekTo(position: Long) = mediaController?.transportControls?.seekTo(position)
    fun rewind() = mediaController?.transportControls?.rewind()
    fun fastForward() = mediaController?.transportControls?.fastForward()

    fun play() = mediaController?.transportControls?.play()
    fun play(track: Track) {
        GlobalScope.launch {
            val podcastName = mediaRepository.getPodcastName(track.collectionId) ?: ""
            val artworkUrl = mediaRepository.getArtworkUri(track.collectionId)
            val extras = Bundle().also {
                it.putParcelable(BUNDLE_KEY_DESCRIPTION, createDescription(podcastName, track, artworkUrl))
            }
            mediaController?.sendCommand(MediaSessionCallback.COMMAND_PLAY, extras, null)
        }
    }

    private fun createDescription(name: String, track: Track, artworkUrl: String?) = track.let {
        MediaDescriptionCompat.Builder()
            .setMediaId(it.trackId.toString())
            .setMediaUri(Uri.parse(it.url))
            .setTitle(it.title)
            .setSubtitle(name)
            .setDescription(it.description)
            .applyIf(artworkUrl != null) { setIconUri(Uri.parse(artworkUrl)) }
            .build()
    }

    fun setControllerCallback(callback: MediaControllerCompat.Callback) {
        controllerCallback = callback
    }
}