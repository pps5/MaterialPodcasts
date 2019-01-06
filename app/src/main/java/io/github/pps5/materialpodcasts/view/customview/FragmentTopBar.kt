package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.content.res.TypedArray
import android.databinding.DataBindingUtil
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.CustomviewFragmentTopbarBinding
import io.github.pps5.materialpodcasts.view.viewmodel.TopBarViewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FragmentTopBar : AppBarLayout, KoinComponent {

    companion object {
        private const val ELEVATION_IN_DP = 4
    }

    private val elevationInPx: Float by lazy { ELEVATION_IN_DP * context.resources.displayMetrics.density }
    private lateinit var binding: CustomviewFragmentTopbarBinding

    private val viewModel: TopBarViewModel by inject()

    var searchBarListener: SearchBarListener? = null
    var onClickNavigateUp: (() -> Unit)? = null
    val scrollChangeListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val m = recyclerView.layoutManager
            if (m is LinearLayoutManager) {
                elevation = if (m.findFirstCompletelyVisibleItemPosition() == 0) 0F else elevationInPx
            }
            viewModel.onScrollChanged(recyclerView.computeVerticalScrollOffset().toFloat())
        }
    }

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
        viewModel.also {
            it.title = typedArray.getString(R.styleable.FragmentTopBar_topBarTitle)
            it.shouldShowNavigateUp = typedArray.getBoolean(R.styleable.FragmentTopBar_showNavigateUp, false)
            it.shouldShowSearchBar = typedArray.getBoolean(R.styleable.FragmentTopBar_searchBarEnabled, false)
            it.shouldShowTitleOnScroll = typedArray.getBoolean(R.styleable.FragmentTopBar_showTitleOnScroll, false)
        }
        binding.viewModel = viewModel
        if (viewModel.shouldShowSearchBar) {
            setUpSearchBar()
        }
        binding.topBarBack.setOnClickListener { onClickNavigateUp?.invoke() }
    }

    private fun setUpSearchBar() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEditText.let {
            it.requestFocus()
            it.post { imm.showSoftInput(it, 0) }
            it.addTextChangedListener(searchBarWatcher)
            it.setOnKeyListener { v, keyCode, event ->
                if (event.action == ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    imm.hideSoftInputFromWindow(v.windowToken, RESULT_UNCHANGED_SHOWN)
                    searchBarListener?.onEnterSearchBar(binding.searchEditText.text.toString())
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
        binding.buttonDeleteAll.setOnClickListener { binding.searchEditText.editableText.clear() }
    }

    private val searchBarWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrEmpty()) {
                binding.buttonDeleteAll.visibility = GONE
                searchBarListener?.onDeleteQuery()
            } else {
                binding.buttonDeleteAll.visibility = VISIBLE
                searchBarListener?.afterTextChanged(s!!.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    interface SearchBarListener {
        fun onEnterSearchBar(text: String)
        fun afterTextChanged(text: String)
        fun onDeleteQuery()
    }
}

