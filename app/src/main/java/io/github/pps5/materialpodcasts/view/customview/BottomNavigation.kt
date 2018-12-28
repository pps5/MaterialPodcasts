package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.SheetCallbackMediator
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class BottomNavigation : BottomNavigationView, KoinComponent {

    companion object {
        private val TAG = BottomNavigation::class.java.simpleName
        private const val BUNDLE_KEY_OLD_STATE = "old_state"
        private const val BUNDLE_KEY_SUPER_STATE = "superState"
    }

    private var oldState = COLLAPSED
    private val callbackMediator: SheetCallbackMediator by inject()

    private val onGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            initializeCallback()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().also {
            it.putSerializable(BUNDLE_KEY_OLD_STATE, oldState)
            it.putParcelable(BUNDLE_KEY_SUPER_STATE, super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState = state.getParcelable<Parcelable>(BUNDLE_KEY_SUPER_STATE)
            oldState = state.getSerializable(BUNDLE_KEY_OLD_STATE) as PanelState? ?: COLLAPSED
            super.onRestoreInstanceState(superState)
        }
    }

    private fun initializeCallback() {
        val parentLayoutHeight = (parent as View).findViewById<View>(R.id.base_container).height
        val originalHeight = resources.getDimension(R.dimen.bottom_navigation_height)
        val originalTop = parentLayoutHeight - originalHeight
        y = if (oldState == EXPANDED) parentLayoutHeight.toFloat() else originalTop

        callbackMediator.setBottomNavCallback(object : SheetCallbackMediator.Callback {
            override fun onSlide(slideOffset: Float) {
                this@BottomNavigation.y = originalTop + (originalHeight * slideOffset)
            }

            override fun onStateChanged(newState: PanelState) {
                oldState = newState
            }
        })
    }
}
