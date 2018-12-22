package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.content.res.TypedArray
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.NestedScrollView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import android.widget.TextView
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.CustomviewFragmentTopbarBinding

class FragmentTopBar : AppBarLayout {

    companion object {
        private const val ELEVATION_IN_DP = 4
    }

    private val elevationInPx: Float by lazy { ELEVATION_IN_DP * context.resources.displayMetrics.density }
    private lateinit var binding: CustomviewFragmentTopbarBinding
    var searchBarEnterAction: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.customview_fragment_topbar, this, true)
        context.theme.obtainStyledAttributes(attrs, R.styleable.FragmentTopBar, 0, 0).also {
            setUpView(it)
            it.recycle()
        }
    }

    private fun setUpView(typedArray: TypedArray) {
        val viewModel = TopBarViewModel(
                title = typedArray.getString(R.styleable.FragmentTopBar_topBarTitle),
                shouldShowNavigateUp = typedArray.getBoolean(R.styleable.FragmentTopBar_showNavigateUp, false),
                shouldShowSearchBar = typedArray.getBoolean(R.styleable.FragmentTopBar_searchBarEnabled, false)
        )
        binding.viewModel = viewModel
        if (viewModel.shouldShowSearchBar) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            binding.searchEditText.let {
                it.requestFocus()
                it.post { imm.showSoftInput(it, 0) }
                it.addTextChangedListener(searchBarWatcher)
                it.setOnKeyListener { v, keyCode, event ->
                    if (event.action == ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                        imm.hideSoftInputFromWindow(v.windowToken, RESULT_UNCHANGED_SHOWN)
                        searchBarEnterAction?.invoke()
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
            }
            binding.buttonDeleteAll.setOnClickListener { binding.searchEditText.editableText.clear() }
        }
    }

    val scrollChangeListener = NestedScrollView.OnScrollChangeListener { _, _, scrollY: Int, _, _ ->
        this.elevation = if (scrollY > 0) elevationInPx else 0F
    }

    private val searchBarWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.buttonDeleteAll.visibility = if (s?.length ?: 0 == 0) GONE else VISIBLE
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // no-op
        }
    }

    inner class TopBarViewModel(
            val title: String? = null,
            val shouldShowNavigateUp: Boolean = false,
            val shouldShowSearchBar: Boolean = false
    ) {
        private val density: Float by lazy { context.resources.displayMetrics.density }
        val topBarTitleTextSize: Float
            get() = if (shouldShowNavigateUp) {
                resources.getDimension(R.dimen.font_size_title_with_navigation) / density
            } else {
                resources.getDimension(R.dimen.font_size_title) / density
            }

        @BindingAdapter("app:textSize")
        fun TextView.setTitleSize(size: Float) {
            textSize = size
        }

        init {
            if (title != null && shouldShowSearchBar) {
                throw IllegalArgumentException("Title and search bar cannot show at the same time")
            }
        }
    }
}

