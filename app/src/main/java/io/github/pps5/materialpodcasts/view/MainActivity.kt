package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.databinding.FragmentSearchBinding
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment
import io.github.pps5.materialpodcasts.view.navigator.MainNavigator
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), MainNavigator, SearchFragment.FragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding
    private val sheetCallbackMediator: SheetCallbackMediator by inject()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> showSearchOverlay()
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onBackPressed() = if (isOverlayVisible) back() else super.onBackPressed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.slidingUpPanel.addPanelSlideListener(sheetCallbackMediator.slideListener)
        binding.nowPlayingSheet.initialize(binding.slidingUpPanel, BottomSheetViewModel())
    }

    override fun removeSearchFragment() = hideSearchOverlay()
    override fun addDetailFragment(fragment: Fragment) = addOverlay(fragment)

    override fun MainActivity.getActivityBinding() = binding

}
