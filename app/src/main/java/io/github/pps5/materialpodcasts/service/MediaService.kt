package io.github.pps5.materialpodcasts.service

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import io.github.pps5.materialpodcasts.util.HasNotificationAction

/**
 * Service used as only MediaService
 */
class MediaService : MediaBrowserServiceCompat(), HasNotificationAction {

    companion object {
        private val TAG = MediaService::class.java.simpleName
        private const val ROOT_ID = "root"
        private const val UPDATE_INTERVAL_IN_MILLIS = 500L
    }

    private var isPlaying = false
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioManager: AudioManager
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSessionCallback: MediaSessionCallback
    private lateinit var updateHandler: Handler
    private lateinit var notificationManager: AppNotificationManager

    /**
     * State change listener for ExoPlayer
     */
    private val playerStateChangedListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updatePlaybackState()
        }
    }

    /**
     * Update playback state: ExoPlayer -> MediaBrowserService
     */
    private val playbackStateUpdater = object : Runnable {

        private fun update(metadata: MediaMetadataCompat) {
            val metadataDuration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            val mediaDuration = exoPlayer.duration
            if (metadataDuration != mediaDuration) {
                val newMetadata = MediaMetadataCompat.Builder(metadata)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaDuration)
                    .build()
                mediaSession.setMetadata(newMetadata)
            }
            updatePlaybackState()
        }

        override fun run() {
            mediaSession.controller.metadata?.let { update(it) }
            updateHandler.postDelayed(this, UPDATE_INTERVAL_IN_MILLIS)
        }
    }

    /**
     * Receive event from MediaController and crate notification if need
     */
    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        private var lastState = PlaybackStateCompat.STATE_NONE

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state?.state != lastState) {
                val newState = state?.state ?: return
                updateNotification(state)
                lastState = newState
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaSession.controller.playbackState?.let {
                updateNotification(it)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext)
        exoPlayer.addListener(playerStateChangedListener)
        setUpMediaSession()
        updateHandler = Handler()
        updateHandler.postDelayed(playbackStateUpdater, UPDATE_INTERVAL_IN_MILLIS)
        notificationManager = AppNotificationManager(this)
    }

    override fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
        updateHandler.removeCallbacksAndMessages(null)
        exoPlayer.removeListener(playerStateChangedListener)
        exoPlayer.stop()
        exoPlayer.release()
    }

    private fun setUpMediaSession() {
        mediaSession = MediaSessionCompat(applicationContext, TAG).also {
            it.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
        }
        mediaSessionCallback = MediaSessionCallback(mediaSession, audioManager, exoPlayer)
        mediaSession.setCallback(mediaSessionCallback)
        sessionToken = mediaSession.sessionToken
        mediaSession.controller.registerCallback(mediaControllerCallback)
    }

    override fun onLoadChildren(mediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        TODO("Use MediaService only to keep media session and exoplayer, " +
            "so it has no MediaBrowser functionality")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(ROOT_ID, null)
    }

    /**
     * Update notification and start/stop foreground service if need
     */
    private fun updateNotification(playbackState: PlaybackStateCompat) {
        val metadata = mediaSession.controller.metadata
        if (metadata == null || !mediaSession.isActive) {
            return
        }
        val notification = notificationManager
            .createMediaNotification(metadata.description, isPlaying)
        when (playbackState.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(AppNotificationManager.NOTIFICATION_ID, notification)
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                stopForeground(false)
                notificationManager.notify(notification)
            }
            PlaybackStateCompat.STATE_STOPPED -> stopForeground(false)
        }
    }

    /**
     * Set playback state for media session from exoplayer state
     */
    private fun updatePlaybackState() {
        val state = when (exoPlayer.playbackState) {
            Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
            Player.STATE_READY ->
                if (exoPlayer.playWhenReady) {
                    PlaybackStateCompat.STATE_PLAYING
                } else {
                    PlaybackStateCompat.STATE_PAUSED
                }
            Player.STATE_ENDED, Player.STATE_IDLE -> PlaybackStateCompat.STATE_STOPPED
            else -> PlaybackStateCompat.STATE_NONE
        }
        isPlaying = state == PlaybackStateCompat.STATE_PLAYING
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_STOP)
            .setState(state, exoPlayer.currentPosition, exoPlayer.playbackParameters.speed)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

}
