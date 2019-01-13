package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import android.widget.TextView
import io.github.pps5.materialpodcasts.R

class TopBarViewModel(context: Context) : ViewModel() {

    private val density = context.resources.displayMetrics.density
    private val fontSize = context.resources.getDimension(R.dimen.font_size_title)
    private val fontSizeWithNavigateUp = context.resources.getDimension(R.dimen.font_size_title_with_navigation)
    private val alphaScrollOffset = context.resources.getDimensionPixelSize(R.dimen.title_alpha_scroll_offset)

    val title = ObservableField<String>()
    var shouldShowNavigateUp: Boolean = false
    var shouldShowSearchBar: Boolean = false
    var shouldShowTitleOnScroll: Boolean = false
        set(value) {
            titleAlpha.set(if (value) 0F else 1F)
            field = value
        }
    val titleAlpha = ObservableFloat()

    val topBarTitleTextSize: Float
        get() = (if (shouldShowNavigateUp) fontSizeWithNavigateUp else fontSize) / density

    @BindingAdapter("app:textSize")
    fun TextView.setTitleSize(size: Float) {
        textSize = size
    }

    fun onScrollChanged(scrollOffset: Float) = titleAlpha.set(scrollOffset / alphaScrollOffset)
}