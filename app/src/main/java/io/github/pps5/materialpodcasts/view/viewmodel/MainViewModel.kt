package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.text.HtmlCompat
import android.text.Spanned
import android.widget.SeekBar
import io.github.pps5.materialpodcasts.service.MediaSubscriber
import io.github.pps5.materialpodcasts.view.customview.PlayingView
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel

data class PlayingData(
    val title: String,
    val podcastName: String,
    val artwork: Bitmap?,
    val description: Spanned,
    val duration: Long
)


class MainViewModel : ViewModel(), PlayingView.PlayingViewModel, SlidingPanel.OnSlideListener {

    private var mediaSubscriber: MediaSubscriber? = null
    private var isTracking = false

    private val _panelState = MutableLiveData<SlidingPanel.PanelState>()
    override val panelState: LiveData<SlidingPanel.PanelState>
        get() = _panelState

    private val _slideOffset = MutableLiveData<Float>()
    override val slideOffset: LiveData<Float>
        get() = _slideOffset

    private val _playingData = MutableLiveData<PlayingData>()
    override val playingData: LiveData<PlayingData>
        get() = _playingData

    private val _position = MutableLiveData<Long>()
    override val position: LiveData<Long>
        get() = _position

    private val _isPlaying = MutableLiveData<Boolean>()
    override val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    override val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) _position.postValue(progress.toLong())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = let { isTracking = true }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.progress?.let { mediaSubscriber?.seekTo(it.toLong()) }
            isTracking = false
        }
    }

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            val data = metadata?.description ?: return
            val description = HtmlCompat.fromHtml(data.description.toString(),
                HtmlCompat.FROM_HTML_MODE_COMPACT
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM)
            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            _playingData.postValue(PlayingData(data.title.toString(), data.subtitle.toString(),
                data.iconBitmap, description, duration))
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            val currentPosition = state?.position ?: return
            val duration = _playingData.value?.duration ?: 0L
            if (!isTracking && duration > 0) {
                _position.postValue(currentPosition)
            }
            _isPlaying.postValue(state.state == PlaybackStateCompat.STATE_PLAYING)
        }
    }

    override fun onSlide(slideOffset: Float) = _slideOffset.postValue(slideOffset)

    override fun onStateChanged(newState: SlidingPanel.PanelState) = _panelState.postValue(newState)

    fun onStart(mediaSubscriber: MediaSubscriber) {
        this.mediaSubscriber = mediaSubscriber
        mediaSubscriber.setControllerCallback(controllerCallback)
    }

    fun onStop() = let { this.mediaSubscriber = null }

}