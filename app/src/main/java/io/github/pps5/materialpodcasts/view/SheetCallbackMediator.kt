package io.github.pps5.materialpodcasts.view

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Mediate between original BottomSheetCallback and the other view callbacks
 */
class SheetCallbackMediator {

    private var bottomNavCallback: Callback? = null
    private var nowPlayingSheetCallback: Callback? = null

    val slideListener = object : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            nowPlayingSheetCallback?.onSlide(slideOffset)
            bottomNavCallback?.onSlide(slideOffset)
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            if (newState != null) {
                nowPlayingSheetCallback?.onStateChanged(newState)
                bottomNavCallback?.onStateChanged(newState)
            }
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
        fun onStateChanged(newState: SlidingUpPanelLayout.PanelState)
    }
}
