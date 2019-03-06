package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.COLLAPSED
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.EXPANDED
import org.koin.standalone.KoinComponent


class BottomNavigation : BottomNavigationView, KoinComponent, SlidingPanel.OnSlideListener {

    companion object {
        private val TAG = BottomNavigation::class.java.simpleName
        private const val BUNDLE_KEY_OLD_STATE = "old_state"
        private const val BUNDLE_KEY_SUPER_STATE = "superState"
    }

    private var defaultTop = 0
    private var shouldHandleSlideEvent = true
    private var oldState = COLLAPSED
    private val navigationHeight = resources.getDimensionPixelOffset(R.dimen.bottom_navigation_height)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (defaultTop == 0) {
            defaultTop = top
            y = if (oldState == EXPANDED) (defaultTop + navigationHeight).toFloat() else defaultTop.toFloat()
        }

    }

    override fun onSlide(slideOffset: Float) {
        if (shouldHandleSlideEvent) {
            this@BottomNavigation.y = defaultTop + (navigationHeight * slideOffset)
        }
    }

    override fun onStateChanged(newState: PanelState) {
        if (shouldHandleSlideEvent) {
            oldState = newState
        }
    }
}
