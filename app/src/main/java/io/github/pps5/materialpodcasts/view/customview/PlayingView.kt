package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.text.HtmlCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.OnClickListener
import android.widget.SeekBar
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.CustomviewPlayingViewBinding
import io.github.pps5.materialpodcasts.service.MediaSubscriber
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PlayingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet), SlidingPanel.OnSlideListener, KoinComponent {

    private val format by lazy { context.getString(R.string.position_format) }
    private val mediaSubscriber: MediaSubscriber by inject()
    private var isSeeking = false
    private var isPlaying = false
    private var slidingPanel: SlidingPanel? = null

    private val binding: CustomviewPlayingViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.customview_playing_view,
        this, true
    )

    private val controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            val description = metadata?.description ?: return

            binding.artwork.setImageBitmap(description.iconBitmap)
            binding.description.text = HtmlCompat.fromHtml(
                description.description.toString(),
                HtmlCompat.FROM_HTML_MODE_COMPACT
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    and HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM)
            binding.title.text = description.title
            binding.podcastName.text = description.subtitle

            metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).let { duration ->
                if (duration > 0) {
                    binding.duration.text = getFormattedTime(duration)
                    binding.seekbar.max = duration.toInt()
                }
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            val currentPosition = state?.position ?: return
            if (binding.seekbar.max > 0 && !isSeeking) {
                binding.seekbar.progress = currentPosition.toInt()
                binding.currentPosition.text = getFormattedTime(currentPosition)
            }
            isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
            binding.isPlaying = isPlaying
        }

    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                binding.currentPosition.text = getFormattedTime(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = let { isSeeking = true }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.progress?.let { mediaSubscriber.seekTo(it.toLong()) }
            isSeeking = false
        }
    }

    private val onClickPlayPause = OnClickListener {
        if (isPlaying) mediaSubscriber.pause() else mediaSubscriber.play()
    }

    init {
        mediaSubscriber.setControllerCallback(controllerCallback)
        binding.let {
            it.isPlaying = isPlaying
            it.seekbar.setOnSeekBarChangeListener(seekBarListener)
            it.headerPlayPause.setOnClickListener(onClickPlayPause)
            it.headerCollapse.setOnClickListener { slidingPanel?.panelState = SlidingPanel.PanelState.COLLAPSED }
            it.playPause.setOnClickListener(onClickPlayPause)
            it.rewind.setOnClickListener { mediaSubscriber.rewind() }
            it.fastForward.setOnClickListener { mediaSubscriber.fastForward() }
        }
    }

    private fun getFormattedTime(timeInMillis: Long) =
        String.format(format, timeInMillis / 60_000, timeInMillis % 60_000 / 1000)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            binding.let {
                val onContent = it.playingContent.top <= ev.y && ev.y <= it.controllerBackground.top
                val canScrollUp = it.playingContent.canScrollVertically(-1)
                requestDisallowInterceptTouchEvent(onContent && canScrollUp)
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    fun setSlidingPanel(slidingPanel: SlidingPanel) {
        this.slidingPanel = slidingPanel
    }

    override fun onSlide(slideOffset: Float) = let { binding.slideOffset = slideOffset }
    override fun onStateChanged(newState: SlidingPanel.PanelState) { /* no-op */
    }
}