package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.*
import kotlin.math.max
import kotlin.math.min

private const val SENSITIVITY = 1f

class SlidingPanel @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    enum class PanelState {
        EXPANDED, COLLAPSED, PEEK_HEIGHT_CHANGING
    }

    private var isFirstLayout = true

    private val viewDragHelper = ViewDragHelper.create(this, SENSITIVITY, Callback())
    private lateinit var content: View
    var panelState: PanelState = COLLAPSED
        set(value) {
            if (field == value) return
            field = value
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount != 1) {
            throw IllegalStateException("SlidingPanel must have exactly 1 child")
        }
        if (isFirstLayout) {
            slideOffset = when (panelState) {
                EXPANDED -> 1f
                COLLAPSED -> 0f
                else -> 0f
            }
        }
        isFirstLayout = false
        content = getChildAt(0)
        val (widthSize, heightSize) = MeasureSpec.getSize(widthMeasureSpec) to MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        content.measure(
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        )
        slideRange = heightSize - panelHeight - currentNavHeight
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        computePanelTop(slideOffset).toInt().let {
            content.layout(0, it, content.measuredWidth, it + measuredHeight)
        }
    }

    private fun computePanelTop(targetSlideOffset: Float): Float {
        val slidePxOffset = targetSlideOffset * slideRange
        return measuredHeight - panelHeight - currentNavHeight - slidePxOffset
    }

    private fun computeSlideOffset(top: Int) = (computePanelTop(0f) - top) / slideRange

    private fun smoothSlideTo(slideOffset: Float) {
        val newTop = computePanelTop(slideOffset).toInt()
        if (viewDragHelper.smoothSlideViewTo(content, 0, newTop)) {
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
        return when (ev?.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewDragHelper.cancel()
                false
            }
            else -> viewDragHelper.shouldInterceptTouchEvent(ev!!)
        }
    }

    private inner class Callback : ViewDragHelper.Callback() {
        override fun tryCaptureView(p0: View, p1: Int) = p0 === content && panelState != PEEK_HEIGHT_CHANGING
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
}