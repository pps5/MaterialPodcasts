package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.github.pps5.materialpodcasts.extension.ContextExtension

class NowPlayingView : FrameLayout, ContextExtension {

    companion object {
        private val TAG = NowPlayingView::class.java.simpleName
    }

    private var callbackMediator: CallbackMediator? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        bottomSheetBehavior = BottomSheetBehavior.from(this)
        bottomSheetBehavior.peekHeight = context.getBottomNavigationHeight() + context.getActionBarSize()
    }

    fun setCallbackMediator(callbackMediator: CallbackMediator) {
        if (this.callbackMediator == null) {
            this.callbackMediator = callbackMediator
        }
        bottomSheetBehavior.setBottomSheetCallback(callbackMediator.bottomSheetCallback)
    }

    /**
     * Mediate between original BottomSheetCallback and the other view callbacks
     */
    class CallbackMediator {

        private var bottomNavCallback: Callback? = null
        private var nowPlayingCallback: Callback? = null

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                nowPlayingCallback?.onSlide(slideOffset)
                bottomNavCallback?.onSlide(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                nowPlayingCallback?.onStateChanged(newState)
                bottomNavCallback?.onStateChanged(newState)
            }
        }

        fun setBottomNavCallback(callback: Callback) {
            bottomNavCallback = callback
        }

        interface Callback {
            fun onSlide(slideOffset: Float)
            fun onStateChanged(newState: Int)
        }
    }
}