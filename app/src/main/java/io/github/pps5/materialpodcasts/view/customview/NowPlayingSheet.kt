package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.NowPlayingSheetBinding
import io.github.pps5.materialpodcasts.view.SheetCallbackMediator
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NowPlayingSheet : FrameLayout, KoinComponent {

    companion object {
        private val TAG = NowPlayingSheet::class.java.simpleName
    }

    private lateinit var binding: NowPlayingSheetBinding
    private lateinit var viewModel: BottomSheetViewModel
    private var panelState = COLLAPSED
    private var isBackKeyEnabled = true
    private val callbackMediator: SheetCallbackMediator by inject()

    private val sheetCallback: SheetCallbackMediator.Callback = object : SheetCallbackMediator.Callback {
        override fun onSlide(slideOffset: Float) {
            // no-op
        }

        override fun onStateChanged(newState: SlidingUpPanelLayout.PanelState) {
            panelState = newState
            val control = if (viewModel.isPlaying) binding.controlPause else binding.controlPlay
            when {
                newState == EXPANDED -> showWithAnimation(binding.sheetClose)
                newState == COLLAPSED -> showWithAnimation(control)
                binding.sheetClose.visibility == VISIBLE -> hideWithAnimation(binding.sheetClose)
                control.visibility == VISIBLE -> hideWithAnimation(control)
            }
        }

        private fun showWithAnimation(view: View) {
            view.clearAnimation()
            view.animate().setDuration(300L)
                    .alpha(1f)
                    .withStartAction { view.visibility = VISIBLE }
                    .withEndAction { view.isClickable = true }
                    .start()
            isBackKeyEnabled = true
        }

        private fun hideWithAnimation(view: View) {
            view.clearAnimation()
            view.animate().alpha(0f).setDuration(200L)
                    .withStartAction { view.isClickable = false }
                    .withEndAction { view.visibility = GONE }
                    .start()
            isBackKeyEnabled = false
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.now_playing_sheet, this, true)
        callbackMediator.setNowPlayingSheetCallback(sheetCallback)
    }

    fun initialize(slidingUpPanelLayout: SlidingUpPanelLayout, viewModel: BottomSheetViewModel) {
        this.viewModel = viewModel
        binding.root.let {
            it.isFocusableInTouchMode = true
            it.setOnKeyListener { _, keyCode, event ->
                val isPressedBackKey = (keyCode == KEYCODE_BACK) && (event.action == ACTION_UP)
                if (isPressedBackKey && panelState == EXPANDED) {
                    slidingUpPanelLayout.panelState = COLLAPSED
                    isBackKeyEnabled = false
                    return@setOnKeyListener true
                }
                return@setOnKeyListener !isBackKeyEnabled
            }
        }
        binding.sheetClose.setOnClickListener { slidingUpPanelLayout.panelState = COLLAPSED }
    }
}