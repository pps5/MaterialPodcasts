package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import android.widget.FrameLayout
import io.github.pps5.materialpodcasts.databinding.NowPlayingSheetBinding
import io.github.pps5.materialpodcasts.extension.ContextExtension
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel

class NowPlayingSheet : FrameLayout, ContextExtension {

    companion object {
        private val TAG = NowPlayingSheet::class.java.simpleName
    }

    private lateinit var binding: NowPlayingSheetBinding
    private lateinit var viewModel: BottomSheetViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var isBackKeyEnabled = true
    private var callbackMediator: CallbackMediator? = null

    private val sheetCallback: CallbackMediator.Callback = object : CallbackMediator.Callback {
        override fun onSlide(slideOffset: Float) {
            // no-op
        }

        override fun onStateChanged(newState: Int) {
            val control = if (viewModel.isPlaying) binding.controlPause else binding.controlPlay
            when {
                newState == STATE_EXPANDED -> showWithAnimation(binding.sheetClose)
                newState == STATE_COLLAPSED -> showWithAnimation(control)
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
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        bottomSheetBehavior = BottomSheetBehavior.from(this)
        bottomSheetBehavior.peekHeight = context.getBottomNavigationHeight() + context.getActionBarSize()
    }

    fun initialize(callbackMediator: CallbackMediator,
                   binding: NowPlayingSheetBinding, viewModel: BottomSheetViewModel) {
        this.binding = binding
        this.viewModel = viewModel
        if (this.callbackMediator == null) {
            callbackMediator.setNowPlayingSheetCallback(sheetCallback)
            this.callbackMediator = callbackMediator
            bottomSheetBehavior.setBottomSheetCallback(callbackMediator.bottomSheetCallback)
        }
        setListeners()
    }

    private fun setListeners() {
        binding.nowPlayingSheet.let {
            it.isFocusableInTouchMode = true
            it.setOnKeyListener { _, keyCode, keyEvent ->
                val isPressedBackKey = (keyCode == KEYCODE_BACK) && (keyEvent.action == ACTION_UP)
                if (isPressedBackKey && bottomSheetBehavior.state == STATE_EXPANDED) {
                    bottomSheetBehavior.state = STATE_COLLAPSED
                    isBackKeyEnabled = false
                    return@setOnKeyListener true
                }
                return@setOnKeyListener !isBackKeyEnabled
            }
        }
        binding.sheetClose.setOnClickListener { bottomSheetBehavior.state = STATE_COLLAPSED }
        binding.nowPlayingArea.setOnClickListener { bottomSheetBehavior.state = STATE_EXPANDED }
    }

    /**
     * Mediate between original BottomSheetCallback and the other view callbacks
     */
    class CallbackMediator {

        private var bottomNavCallback: Callback? = null
        private var nowPlayingSheetCallback: Callback? = null

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                nowPlayingSheetCallback?.onSlide(slideOffset)
                bottomNavCallback?.onSlide(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                nowPlayingSheetCallback?.onStateChanged(newState)
                bottomNavCallback?.onStateChanged(newState)
            }
        }

        fun setBottomNavCallback(callback: Callback) {
            bottomNavCallback = callback
        }

        fun setNowPlayingSheetCallback(callback: Callback) {
            nowPlayingSheetCallback = callback
        }

        interface Callback {
            fun onSlide(slideOffset: Float)
            fun onStateChanged(newState: Int)
        }
    }
}