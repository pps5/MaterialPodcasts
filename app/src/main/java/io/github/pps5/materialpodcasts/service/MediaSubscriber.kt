package io.github.pps5.materialpodcasts.service

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.github.pps5.materialpodcasts.service.MediaSessionCallback.Companion.BUNDLE_KEY_DESCRIPTION

class MediaSubscriber(private val context: Context) {

    private lateinit var subscribeInternal: (String) -> Unit
    var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
    private val playlist = mutableListOf<MediaBrowserCompat.MediaItem>()
    private var controllerCallback: MediaControllerCompat.Callback? = null

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

    private var subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            when (children.size) {
                0 -> { // todo: error handling (fetch again?)
                }
                1 -> {
                    val extra = createExtra(children[0].description)
                    mediaController?.transportControls?.playFromMediaId(parentId, extra)
                }
                else -> {
                    playlist.addAll(children)
                    val extra = createExtra(playlist[0].description)
                    mediaController?.transportControls?.playFromMediaId(parentId, extra)
                }
            }
        }
    }

    private fun createExtra(description: MediaDescriptionCompat) = Bundle().also {
        it.putParcelable(BUNDLE_KEY_DESCRIPTION, description)
    }

    fun connect() {
        mediaBrowser = MediaBrowserCompat(context,
            ComponentName(context, MediaService::class.java), connectionCallback, null)
        mediaBrowser?.connect()
        var lastSubscribedId = "/"
        subscribeInternal = { mediaId: String ->
            mediaBrowser?.let {
                synchronized(lastSubscribedId) {
                    it.unsubscribe(lastSubscribedId)
                    lastSubscribedId = mediaId
                    it.subscribe(mediaId, subscriptionCallback)
                }
            }
        }
    }

    fun play() = mediaController?.transportControls?.play()
    fun pause() = mediaController?.transportControls?.pause()
    fun seekTo(position: Long) = mediaController?.transportControls?.seekTo(position)
    fun rewind() = mediaController?.transportControls?.rewind()
    fun fastFoward() = mediaController?.transportControls?.fastForward()

    fun disconnect() = mediaBrowser?.disconnect()

    fun subscribe(mediaId: String) = subscribeInternal(mediaId)

    fun setControllerCallback(callback: MediaControllerCompat.Callback) {
        controllerCallback = callback
    }
}