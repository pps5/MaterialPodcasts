package io.github.pps5.materialpodcasts.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.extension.applyIf
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.repository.MediaRepository
import io.github.pps5.materialpodcasts.service.NotificationDeleteReceiver.Companion.DELETE_NOTIFICATION
import io.github.pps5.materialpodcasts.util.HasNotificationAction
import io.github.pps5.materialpodcasts.util.HasNotificationAction.NotificationType
import io.github.pps5.materialpodcasts.view.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MediaService : MediaBrowserServiceCompat(), HasNotificationAction {

    companion object {
        private val TAG = MediaService::class.java.simpleName
        private const val ROOT_ID = "root"
        private const val UPDATE_INTERVAL_IN_MILLIS = 500L
    }

    private val mediaRepository: MediaRepository by inject()

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioManager: AudioManager
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSessionCallback: MediaSessionCallback
    private lateinit var updateExecutor: ScheduledExecutorService

    private val playerStateChangedListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updatePlaybackState()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext)
        exoPlayer.addListener(playerStateChangedListener)
        setUpMediaSession()
        setUpPlaybackUpdater()
    }

    override fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
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
        mediaSession.controller.registerCallback(object : MediaControllerCompat.Callback() {
            private var lastState = PlaybackStateCompat.STATE_NONE

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                if (state?.state != lastState) {
                    createNotification()
                }
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                createNotification()
            }
        })
    }

    private fun setUpPlaybackUpdater() {
        updateExecutor = Executors.newScheduledThreadPool(1)
        updateExecutor.scheduleAtFixedRate({
            mediaSession.controller.metadata?.let {
                val metadataDuration = it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                val mediaDuration = exoPlayer.duration
                if (metadataDuration != mediaDuration) {
                    val newMetadata = MediaMetadataCompat.Builder(it)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaDuration)
                        .build()
                    mediaSession.setMetadata(newMetadata)
                }
            }
            updatePlaybackState()
        }, 0, UPDATE_INTERVAL_IN_MILLIS, TimeUnit.MILLISECONDS)
    }

    override fun onLoadChildren(mediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        val idSplit = mediaId.split("/")
        if (mediaId == mediaRepository.getRoot() || idSplit.size != 2) {
            result.sendResult(mutableListOf())
            return
        }
        result.detach()
        GlobalScope.launch {
            val (collectionId, trackNumber) = idSplit[0].toLong() to idSplit[1].toInt()
            Log.d(TAG, "collectionId: $collectionId, trackNumber: $trackNumber")
            val nameAndTrack = mediaRepository.getTrackAndPodcastName(collectionId, trackNumber)
            if (nameAndTrack == null) {
                result.sendResult(mutableListOf())
            } else {
                val (name, track) = nameAndTrack
                Log.d(TAG, "$track,  ${mediaRepository.getArtworkUri(collectionId)}")
                result.sendResult(mutableListOf(
                    createMediaItem(name, track, mediaRepository.getArtworkUri(collectionId))
                ))
            }
        }
    }

    private fun createMediaItem(name: String, track: Track, artworkUrl: String?) = track.let {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(it.trackId.toString())
            .setMediaUri(Uri.parse(it.url))
            .setTitle(it.title)
            .setSubtitle(name)
            .setDescription(it.description)
            .applyIf(artworkUrl != null) { setIconUri(Uri.parse(artworkUrl)) }
            .build()
        MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        Log.d(TAG, "Connected from: $clientPackageName ($clientUid)")
        return BrowserRoot(ROOT_ID, null)
    }

    private fun createNotification() {
        val controller = mediaSession.controller
        val metadata = controller.metadata
        if (metadata == null || !mediaSession.isActive) return

        val description = metadata.description
        val notification = getNotificationBuilder(applicationContext, NotificationType.Playback)
            .setContentTitle(description.title)
            .setContentText(description.subtitle)
            .setSubText(description.description)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(createContentIntent())
            .setSmallIcon(R.drawable.ic_share_black_24dp)  // todo: replace icon
            .setLargeIcon(description.iconBitmap)
            .setDeleteIntent(PendingIntent.getBroadcast(
                this, 0, Intent(DELETE_NOTIFICATION), 0))
            .addActionsForPlayback(this, controller.playbackState?.state)
            .setStyle(
                android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(1)
            )
            .build()

        startForeground(1, notification)
        if (controller.playbackState?.state != PlaybackStateCompat.STATE_PLAYING) {
            stopForeground(false)
        }
    }

    private fun createContentIntent(): PendingIntent? {
        val i = Intent(this, MainActivity::class.java).also { it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
        return PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_CANCEL_CURRENT)
    }

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

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_STOP)
            .setState(state, exoPlayer.currentPosition, exoPlayer.playbackParameters.speed)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

}
