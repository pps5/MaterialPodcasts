package io.github.pps5.materialpodcasts.extension

import android.content.Context
import android.util.TypedValue
import io.github.pps5.materialpodcasts.R

interface ContextExtension {

    companion object {
        private const val BOTTOM_NAVIGATION_DIMEN_ID = "design_bottom_navigation_height"
    }

    fun Context.getBottomNavigationHeight(): Int {
        val navResId = resources.getIdentifier(BOTTOM_NAVIGATION_DIMEN_ID, "dimen", packageName)
        return if (navResId > 0) resources.getDimensionPixelSize(navResId) else 0
    }

    fun Context.getActionBarSize(): Int {
        with (TypedValue()) {
            return if (theme.resolveAttribute(R.attr.actionBarSize, this, true)) {
                TypedValue.complexToDimensionPixelSize(data, resources.displayMetrics)
            }else {
                0
            }
        }
    }
}