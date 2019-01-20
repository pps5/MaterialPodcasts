package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.widget.TextView

class SimpleWordWrapTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): TextView(context, attrs, defStyleAttr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Android O (8.0) has bug on word wrap
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
        }
    }
}