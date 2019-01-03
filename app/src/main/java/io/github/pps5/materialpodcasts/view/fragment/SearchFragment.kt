package io.github.pps5.materialpodcasts.view.fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.FragmentSearchBinding
import io.github.pps5.materialpodcasts.repository.SearchRepository
import io.github.pps5.materialpodcasts.view.viewmodel.SearchViewModel
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.let {
            it.setLifecycleOwner(this)
            it.viewModel = viewModel
            if (activity is FragmentInteractionListener) {
                it.topBar.onClickNavigateUp = (activity as FragmentInteractionListener)::removeSearchFragment
                it.content.setOnScrollChangeListener(binding.topBar.scrollChangeListener)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.topBar.onClickNavigateUp = null
        viewModel.cancel()
        super.onDestroyView()
    }

    interface FragmentInteractionListener {
        fun removeSearchFragment()
    }
}