package io.github.pps5.materialpodcasts.view.navigator

import android.support.transition.Fade
import android.support.transition.Transition
import android.support.v4.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.extension.withTransaction
import io.github.pps5.materialpodcasts.view.MainActivity
import io.github.pps5.materialpodcasts.view.fragment.PodcastDetailFragment
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment

private var currentFragment: Fragment? = null
private var currentOverlayFragment: Fragment? = null

interface MainNavigator {

    fun MainActivity.getActivityBinding(): ActivityMainBinding

    val MainActivity.isOverlayVisible: Boolean
        get() = supportFragmentManager.findFragmentById(R.id.overlay_container) != null

    /**
     * Show overlay search screen if it is NOT VISIBLE; otherwise do nothing
     */
    fun MainActivity.showSearchOverlay() {
        if (currentOverlayFragment != null) {
            return
        }
        currentOverlayFragment = SearchFragment().also { fragment ->
            getActivityBinding().overlayContainer.let { container ->
                fragment.enterTransition = Fade(Fade.MODE_IN).setDuration(150)
                container.visibility = VISIBLE
                supportFragmentManager.withTransaction { it.add(container.id, fragment) }
            }
        }
    }

    /**
     * Add new fragment to overlay container
     */
    fun MainActivity.addOverlay(fragment: Fragment) {
        supportFragmentManager.withTransaction { it.add(R.id.overlay_container, fragment).addToBackStack(null) }
    }

    fun MainActivity.back() {
        val f = supportFragmentManager.findFragmentById(R.id.overlay_container)
        if (f is SearchFragment) {
            hideSearchOverlay()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    /**
     * Hide overlay search screen if it is VISIBLE; otherwise do nothing
     */
    fun MainActivity.hideSearchOverlay() {
        currentOverlayFragment?.let { fragment ->
            val container = getActivityBinding().overlayContainer
            fragment.exitTransition = Fade(Fade.MODE_OUT)
                    .setDuration(150)
                    .addListener(object : Transition.TransitionListener {
                        override fun onTransitionEnd(p0: Transition) {
                            container.visibility = GONE
                            currentOverlayFragment = null
                        }
                        override fun onTransitionResume(p0: Transition) {}
                        override fun onTransitionPause(p0: Transition) {}
                        override fun onTransitionCancel(p0: Transition) {}
                        override fun onTransitionStart(p0: Transition) {}
                    })
            supportFragmentManager.withTransaction { it.remove(fragment) }
        }
    }
}