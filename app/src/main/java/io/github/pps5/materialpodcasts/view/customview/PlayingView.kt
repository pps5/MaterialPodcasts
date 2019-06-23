package io.github.pps5.materialpodcasts.view.customview

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.SeekBar
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.CustomviewPlayingViewBinding
import io.github.pps5.materialpodcasts.service.MediaSubscriber
import io.github.pps5.materialpodcasts.view.viewmodel.PlayingData
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PlayingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet), KoinComponent {

    private val mediaSubscriber: MediaSubscriber by inject()
    private var slidingPanel: SlidingPanel? = null

    private val binding: CustomviewPlayingViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.customview_playing_view,
        this, true
    )

    init {
        binding.let {
            it.headerCollapse.setOnClickListener { slidingPanel?.panelState = SlidingPanel.PanelState.COLLAPSED }
            it.rewind.setOnClickListener { mediaSubscriber.rewind() }
            it.fastForward.setOnClickListener { mediaSubscriber.fastForward() }
        }
    }

    fun setViewModel(lifecycleOwner: LifecycleOwner, viewModel: PlayingViewModel) {
        binding.setLifecycleOwner(lifecycleOwner)
        binding.viewModel = viewModel
    }

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

    interface PlayingViewModel {
        val panelState: LiveData<SlidingPanel.PanelState>
        val slideOffset: LiveData<Float>
        val playingData: LiveData<PlayingData>
        val position: LiveData<Long>
        val isPlaying: LiveData<Boolean>
        val seekBarChangeListener: SeekBar.OnSeekBarChangeListener
    }
}