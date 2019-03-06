package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.extension.withTransaction
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.COLLAPSED
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment
import io.github.pps5.materialpodcasts.view.fragment.SubscriptionFragment

class MainActivity : AppCompatActivity(), SearchFragment.FragmentInteractionListener {


    private lateinit var binding: ActivityMainBinding

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_dashboard -> supportFragmentManager.withTransaction { replace(R.id.container, SubscriptionFragment()) }
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onBackPressed() = onBack()

    private fun onBack() {
        if (binding.slidingUpPanel.panelState != COLLAPSED) {
            binding.slidingUpPanel.panelState = COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.slidingUpPanel.onSlideListener = binding.navigation
    }

    override fun onClickNavigateUp() = onBack()
    override fun addDetailFragment(fragment: Fragment) {}

}
