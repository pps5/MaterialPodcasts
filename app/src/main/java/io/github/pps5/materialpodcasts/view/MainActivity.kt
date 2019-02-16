package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.extension.withTransaction
import io.github.pps5.materialpodcasts.view.fragment.PodcastDetailFragment
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment
import io.github.pps5.materialpodcasts.view.fragment.SubscriptionFragment
import io.github.pps5.materialpodcasts.view.navigator.OverlayNavigator
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), OverlayNavigator, SearchFragment.FragmentInteractionListener {

    companion object {
        private const val BUNDLE_KEY_OVERLAY_TAG_STACK = "overlay_tag_stack"
    }

    private lateinit var binding: ActivityMainBinding
    private val sheetCallbackMediator: SheetCallbackMediator by inject()

    override val overlayTagStack = ArrayList<String>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> showSearchOverlay()
            R.id.navigation_dashboard -> supportFragmentManager.withTransaction { replace(R.id.container, SubscriptionFragment()) }
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onBackPressed() = onBack()

    private fun onBack() {
        if (!backOnOverlay()) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.slidingUpPanel.addPanelSlideListener(sheetCallbackMediator.slideListener)
        binding.nowPlayingSheet.initialize(binding.slidingUpPanel, BottomSheetViewModel())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        overlayTagStack.addAll(savedInstanceState?.getStringArrayList(BUNDLE_KEY_OVERLAY_TAG_STACK) ?: arrayListOf())
        restoreOverlay()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putStringArrayList(BUNDLE_KEY_OVERLAY_TAG_STACK, overlayTagStack)
        super.onSaveInstanceState(outState)
    }

    override fun onClickNavigateUp() = onBack()
    override fun addDetailFragment(fragment: Fragment) = addOverlay(fragment, PodcastDetailFragment.TAG)

    override fun MainActivity.getActivityBinding() = binding

}
