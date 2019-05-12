package io.github.pps5.materialpodcasts.service

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import io.github.pps5.materialpodcasts.service.MediaSessionCallback.Companion.BUNDLE_KEY_DESCRIPTION
import io.github.pps5.materialpodcasts.view.MainActivity

class MediaSubscriber(private val activity: MainActivity) {

    private lateinit var subscribeInternal: (String) -> Unit
    var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
    private val playlist = mutableListOf<MediaBrowserCompat.MediaItem>()

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(activity, mediaBrowser!!.sessionToken)
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
        mediaBrowser = MediaBrowserCompat(activity,
            ComponentName(activity, MediaService::class.java), connectionCallback, null)
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

    fun disconnect() = mediaBrowser?.disconnect()

    fun subscribe(mediaId: String) = subscribeInternal(mediaId)
}