package io.github.pps5.materialpodcasts.view.navigator

import android.support.transition.Fade
import android.support.transition.Transition
import android.support.v4.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.extension.withTransaction
import io.github.pps5.materialpodcasts.view.MainActivity
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment

private var currentFragment: Fragment? = null
private var currentOverlayFragment: Fragment? = null

interface MainNavigator {

    fun MainActivity.getActivityBinding(): ActivityMainBinding

    val MainActivity.isOverlayVisible: Boolean
        get() = currentOverlayFragment != null

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