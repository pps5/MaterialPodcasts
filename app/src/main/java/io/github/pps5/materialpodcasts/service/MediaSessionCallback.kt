package io.github.pps5.materialpodcasts.service

import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import io.github.pps5.materialpodcasts.repository.MediaRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MediaSessionCallback(
    private val mediaSession: MediaSessionCompat,
    private val audioManager: AudioManager,
    private val exoPlayer: ExoPlayer
) : MediaSessionCompat.Callback(), KoinComponent {

    companion object {
        const val BUNDLE_KEY_DESCRIPTION = "description"
        private const val REWIND_DURATION_IN_MILLIS = 10_000L
        private const val FASTFORWARD_DURATION_IN_MILLIS = 10_000L
    }

    private var audioFocusRequest: AudioFocusRequest? = null
    private val mediaRepository: MediaRepository by inject()

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val description = extras?.getParcelable<MediaDescriptionCompat>(BUNDLE_KEY_DESCRIPTION)
            ?: throw IllegalStateException("Extra must have metadata")
        val mediaSource = ExtractorMediaSource
            .Factory(DefaultHttpDataSourceFactory("exoplayer"))
            .createMediaSource(description.mediaUri)
        exoPlayer.prepare(mediaSource, true, true)
        mediaRepository.fetchArtwork(description.iconUri.toString()) {
            mediaSession.setMetadata(description.toMetadata(it))
            onPlay()
        }
    }

    private fun MediaDescriptionCompat.toMetadata(bitmap: Bitmap?) =
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri.toString())
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, subtitle.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, description.toString())
            .build()

    @Deprecated("Use onPlayFromMediaId")
    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
    }

    override fun onPlay() {
        if (requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaSession.isActive = true
            exoPlayer.playWhenReady = true
        }
    }

    override fun onPause() {
        exoPlayer.playWhenReady = false
        abandonAudioFocus()
    }

    override fun onStop() {
        exoPlayer.playWhenReady = false
        exoPlayer.stop(true)
        abandonAudioFocus()
    }

    override fun onSeekTo(pos: Long) {
        exoPlayer.seekTo(pos)
        val duration = exoPlayer.duration
        when {
            duration == C.TIME_UNSET -> {
            }
            pos < 0 -> exoPlayer.seekTo(0L)
            pos > duration -> exoPlayer.seekTo(duration)
            else -> exoPlayer.seekTo(pos)
        }
    }

    override fun onRewind() = onSeekTo(exoPlayer.currentPosition - REWIND_DURATION_IN_MILLIS)
    override fun onFastForward() = onSeekTo(exoPlayer.currentPosition + FASTFORWARD_DURATION_IN_MILLIS)

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("deprecation")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        val keyEvent = mediaButtonEvent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (keyEvent?.action != KeyEvent.ACTION_DOWN) {
            return super.onMediaButtonEvent(mediaButtonEvent)
        }
        Log.d("dbg", "keyCode: ${keyEvent.keyCode}")
        when (keyEvent.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
            KeyEvent.KEYCODE_MEDIA_PAUSE -> onPause()
            KeyEvent.KEYCODE_MEDIA_STOP -> onStop()
            KeyEvent.KEYCODE_MEDIA_REWIND -> onRewind()
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onFastForward()
            else -> return false
        }
        return true
    }

    @Suppress("deprecation")
    private fun requestAudioFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            audioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    private val audioFocusChangeListener = OnAudioFocusChangeListener { focus: Int ->
        val transportControls = mediaSession.controller.transportControls
        when (focus) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> transportControls.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> TODO("volume down")
            AudioManager.AUDIOFOCUS_GAIN -> transportControls.play()
        }
    }
}