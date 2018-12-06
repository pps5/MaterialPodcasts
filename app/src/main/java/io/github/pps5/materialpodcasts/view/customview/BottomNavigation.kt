package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.extension.ContextExtension


class BottomNavigation : BottomNavigationView, ContextExtension {

    companion object {
        private val TAG = BottomNavigation::class.java.simpleName
        private const val BUNDLE_KEY_OLD_STATE = "old_state"
        private const val BUNDLE_KEY_SUPER_STATE = "superState"
    }

    private var oldState = BottomSheetBehavior.STATE_COLLAPSED
    private var callbackMediator: NowPlayingSheet.CallbackMediator? = null
    private var isCompleteInitialLayout = false

    private val onGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            initializeCallback()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().also {
            it.putInt(BUNDLE_KEY_OLD_STATE, oldState)
            it.putParcelable(BUNDLE_KEY_SUPER_STATE, super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState = state.getParcelable<Parcelable>(BUNDLE_KEY_SUPER_STATE)
            oldState = state.getInt(BUNDLE_KEY_OLD_STATE, BottomSheetBehavior.STATE_COLLAPSED)
            super.onRestoreInstanceState(superState)
        }
    }

    fun setCallbackMediator(mediator: NowPlayingSheet.CallbackMediator) {
        if (callbackMediator == null && !isCompleteInitialLayout) {
            callbackMediator = mediator
            initializeCallback()
        }
    }

    private fun initializeCallback() {
        isCompleteInitialLayout = true
        val parentLayoutHeight = (parent as View).findViewById<View>(R.id.container).height
        val originalHeight = context.getBottomNavigationHeight()
        val originalTop = parentLayoutHeight - originalHeight

        y = if (oldState == BottomSheetBehavior.STATE_EXPANDED) {
            parentLayoutHeight.toFloat()
        } else {
            originalTop.toFloat()
        }

        Log.d(TAG, "Initialize callback: originalTop -> $originalTop")
        callbackMediator?.setBottomNavCallback(object : NowPlayingSheet.CallbackMediator.Callback {
            override fun onSlide(slideOffset: Float) {
                this@BottomNavigation.y = originalTop + (originalHeight * slideOffset)
            }

            override fun onStateChanged(newState: Int) {
                oldState = newState
            }
        })
    }
}
