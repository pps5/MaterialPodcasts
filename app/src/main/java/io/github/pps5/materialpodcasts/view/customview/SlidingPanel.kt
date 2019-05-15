package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.Navigator
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.*
import kotlin.math.max
import kotlin.math.min

private const val SENSITIVITY = 1f

class SlidingPanel @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet), Navigator.InteractionListener {

    enum class PanelState {
        EXPANDED, COLLAPSED, PEEK_HEIGHT_CHANGING
    }

    private var isFirstLayout = true

    private val viewDragHelper = ViewDragHelper.create(this, SENSITIVITY, Callback())
    private lateinit var mainContent: View
    private lateinit var panelContent: View
    var panelState: PanelState = COLLAPSED
        set(value) {
            if (field == value) return
            field = value
            if (isFirstLayout) return
            onSlideListener?.onStateChanged(value)
            when (value) {
                EXPANDED -> smoothSlideTo(1f)
                COLLAPSED -> smoothSlideTo(0f)
                else -> { // no-op
                }
            }
        }

    private val panelHeight = resources.getDimensionPixelOffset(R.dimen.panel_height)
    private val bottomNavHeight = resources.getDimensionPixelOffset(R.dimen.bottom_navigation_height)
    private var currentNavHeight = bottomNavHeight
    private var slideRange: Int = 0
    private var slideOffset: Float = 0f

    var onSlideListener: OnSlideListener? = null
    var onChangeListener: OnPeekHeightChangeListener? = null

    private val expandedTop
        get() = computePanelTop(1f)

    private val collapsedTop
        get() = computePanelTop(0f)

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.isExpanded = panelState == EXPANDED
        savedState.lastBottomNavHeight = currentNavHeight
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        panelState = if (savedState.isExpanded) EXPANDED else COLLAPSED
        currentNavHeight = savedState.lastBottomNavHeight
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount != 2) {
            throw IllegalStateException("SlidingPanel must have exactly 2 children")
        }
        if (isFirstLayout) {
            slideOffset = when (panelState) {
                EXPANDED -> 1f
                COLLAPSED -> 0f
                else -> 0f
            }
        }
        isFirstLayout = false
        mainContent = getChildAt(0)
        panelContent = getChildAt(1)
        val (widthSize, heightSize) = MeasureSpec.getSize(widthMeasureSpec) to MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        mainContent.measure(widthMeasureSpec, heightMeasureSpec)
        panelContent.measure(widthMeasureSpec, heightMeasureSpec)
        slideRange = heightSize - panelHeight - currentNavHeight
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        computePanelTop(slideOffset).toInt().let {
            panelContent.layout(0, it, panelContent.measuredWidth, it + measuredHeight)
        }
        mainContent.layout(0, 0, measuredWidth, mainContent.measuredHeight)
    }

    private fun computePanelTop(targetSlideOffset: Float): Float {
        val slidePxOffset = targetSlideOffset * slideRange
        return measuredHeight - panelHeight - currentNavHeight - slidePxOffset
    }

    private fun computeSlideOffset(top: Int) = (computePanelTop(0f) - top) / slideRange

    private fun smoothSlideTo(slideOffset: Float) {
        val newTop = computePanelTop(slideOffset).toInt()
        if (viewDragHelper.smoothSlideViewTo(panelContent, 0, newTop)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        viewDragHelper.processTouchEvent(event!!)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (computePanelTop(slideOffset) > ev!!.rawY) {
            return false
        }
        return when (ev.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewDragHelper.cancel()
                false
            }
            else -> viewDragHelper.shouldInterceptTouchEvent(ev)
        }
    }

    override fun showBottomNavigation() = changePeekHeight(-bottomNavHeight)
    override fun hideBottomNavigation() = changePeekHeight(+bottomNavHeight)
    override fun shouldHandleNavigationClick() = panelState != PEEK_HEIGHT_CHANGING

    private fun changePeekHeight(changeHeight: Int) {
        val newTop = when {
            panelState == PEEK_HEIGHT_CHANGING && changeHeight > 0 -> measuredHeight - panelHeight
            panelState == PEEK_HEIGHT_CHANGING && changeHeight < 0 -> measuredHeight - panelHeight - bottomNavHeight
            else -> computePanelTop(0f).toInt() + changeHeight
        }
        if (viewDragHelper.smoothSlideViewTo(panelContent, left, newTop)) {
            panelState = PEEK_HEIGHT_CHANGING
            postInvalidateOnAnimation()
        }
    }

    private inner class Callback : ViewDragHelper.Callback() {
        override fun tryCaptureView(p0: View, p1: Int) = p0 === panelContent && panelState != PEEK_HEIGHT_CHANGING
        override fun getViewVerticalDragRange(child: View) = slideRange

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (panelState == PEEK_HEIGHT_CHANGING) {
                val changedSize = top - (measuredHeight - panelHeight - bottomNavHeight)
                val changeOffset = changedSize / bottomNavHeight.toFloat()
                onChangeListener?.onChanged(changeOffset)
                currentNavHeight = bottomNavHeight - (bottomNavHeight * changeOffset).toInt()
                if (changeOffset == 1f || changeOffset == 0f) {
                    slideRange = measuredHeight - panelHeight - currentNavHeight
                    panelState = COLLAPSED
                }
            } else {
                slideOffset = computeSlideOffset(top)
                onSlideListener?.onSlide(slideOffset)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val nextTop = when {
                yvel < 0 && slideOffset > 0.3f
                    || yvel < 0 && slideOffset <= 0.4f -> expandedTop

                yvel > 0 && slideOffset <= 0.3f
                    || yvel > 0 && slideOffset >= 0.4f -> collapsedTop

                slideOffset >= 0.5f -> expandedTop
                slideOffset < 0.5f -> collapsedTop
                else -> collapsedTop
            }
            viewDragHelper.settleCapturedViewAt(releasedChild.left, nextTop.toInt())
            invalidate()
        }

        override fun onViewDragStateChanged(state: Int) {
            if (viewDragHelper.viewDragState == ViewDragHelper.STATE_IDLE) {
                when (slideOffset) {
                    0f -> panelState = COLLAPSED
                    1f -> panelState = EXPANDED
                }
            }
            onSlideListener?.onStateChanged(panelState)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val collapsedTop = computePanelTop(0f)
            val expandedTop = computePanelTop(1f)
            return min(max(top.toFloat(), expandedTop), collapsedTop).toInt()
        }
    }

    interface OnSlideListener {
        fun onSlide(slideOffset: Float)
        fun onStateChanged(newState: PanelState)
    }

    interface OnPeekHeightChangeListener {
        fun onChanged(offset: Float)
    }

    internal class SavedState : View.BaseSavedState {

        var isExpanded: Boolean = false
        var lastBottomNavHeight: Int = 0

        constructor(`in`: Parcel) : super(`in`) {
            isExpanded = `in`.readByte().toInt() == 1
            lastBottomNavHeight = `in`.readInt()
        }

        @Suppress("unused")
        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (isExpanded) 1 else 0)
            out?.writeInt(lastBottomNavHeight)
        }

        companion object {
            @JvmField
            @Suppress("unused")
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel?): SavedState {
                    return SavedState(source!!)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}