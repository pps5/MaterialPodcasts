package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.di.ACTIVITY_SCOPE
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.COLLAPSED
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.PEEK_HEIGHT_CHANGING
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), Navigator.InteractionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topLevelNavigator: Navigator

    override fun onBackPressed() {
        when {
            binding.slidingUpPanel.panelState == PEEK_HEIGHT_CHANGING -> {
            }
            binding.slidingUpPanel.panelState != COLLAPSED -> binding.slidingUpPanel.panelState = COLLAPSED
            supportFragmentManager.backStackEntryCount == 1 -> topLevelNavigator.onBack()
            else -> super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.slidingUpPanel.onSlideListener = binding.navigation
        binding.slidingUpPanel.onChangeListener = binding.navigation
    }

    override fun onResume() {
        super.onResume()
        getKoin().getOrCreateScope(ACTIVITY_SCOPE)
        topLevelNavigator = get { parametersOf(supportFragmentManager, this) }
        binding.navigation.setOnNavigationItemSelectedListener(topLevelNavigator)
    }

    override fun onPause() {
        super.onPause()
        getKoin().scopeRegistry.getScope(ACTIVITY_SCOPE)?.close()
    }

    override fun hideBottomNavigation() = binding.slidingUpPanel.changePeekHeight(+binding.navigation.navigationHeight)
    override fun showBottomNavigation() = binding.slidingUpPanel.changePeekHeight(-binding.navigation.navigationHeight)
    override fun shouldHandleNavigationClick() = binding.slidingUpPanel.panelState != PEEK_HEIGHT_CHANGING

}
