package io.github.pps5.materialpodcasts.view.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.BindingAdapter
import android.widget.TextView
import io.github.pps5.materialpodcasts.R

class TopBarViewModel(context: Context) : ViewModel() {

    private val density = context.resources.displayMetrics.density
    private val fontSize = context.resources.getDimension(R.dimen.font_size_title)
    private val fontSizeWithNavigateUp = context.resources.getDimension(R.dimen.font_size_title_with_navigation)

    var title: String? = null
    var shouldShowNavigateUp: Boolean = false
    var shouldShowSearchBar: Boolean = false

    val topBarTitleTextSize: Float
        get() = (if (shouldShowNavigateUp) fontSizeWithNavigateUp else fontSize) / density

    @BindingAdapter("app:textSize")
    fun TextView.setTitleSize(size: Float) {
        textSize = size
    }
}