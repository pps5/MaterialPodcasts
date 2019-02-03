package io.github.pps5.materialpodcasts.view.navigator

import android.support.transition.Fade
import android.support.transition.Transition
import android.support.v4.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.extension.pop
import io.github.pps5.materialpodcasts.extension.withTransaction
import io.github.pps5.materialpodcasts.view.MainActivity
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment

interface SearchNavigator {

    val overlayTagStack: ArrayList<String>

    fun MainActivity.getActivityBinding(): ActivityMainBinding

    private val MainActivity.currentOverlay
        get() = supportFragmentManager.findFragmentById(R.id.overlay_container)

    val MainActivity.isSearchFragmentVisible: Boolean
        get() {
            val f = supportFragmentManager.findFragmentByTag(SearchFragment.TAG)
            return f != null && !f.isDetached
        }

    fun MainActivity.restoreOverlay() {
        supportFragmentManager.let {
            it.findFragmentByTag(overlayTagStack.lastOrNull())?.let { f -> it.withTransaction { attach(f) } }
        }
        getActivityBinding().overlayContainer.visibility = if (overlayTagStack.isEmpty()) GONE else VISIBLE
    }

    /**
     * Show overlay search screen if it is invisible or gone; otherwise do nothing
     */
    fun MainActivity.showSearchOverlay() {
        if (currentOverlay != null) {
            return
        }
        val fragment = SearchFragment().also { f ->
            f.enterTransition = Fade(Fade.MODE_IN)
                    .setDuration(150)
                    .addListener(SelfRemoveTransitionListener(onEnd = {
                        f.showIme()
                        f.enterTransition = null
                        getActivityBinding().overlayBackground.visibility = VISIBLE
                    }))
        }
        overlayTagStack.add(SearchFragment.TAG)
        getActivityBinding().overlayContainer.visibility = VISIBLE
        supportFragmentManager.withTransaction {
            replace(R.id.overlay_container, fragment, SearchFragment.TAG)
        }
    }

    /**
     * Add fragment to overlay if the tag is not added
     */
    fun MainActivity.addOverlay(fragment: Fragment, tag: String) {
        if (overlayTagStack.contains(tag)) {
            return
        }
        supportFragmentManager.withTransaction {
            supportFragmentManager.findFragmentByTag(overlayTagStack.lastOrNull())?.let {
                it.exitTransition = Fade(Fade.MODE_OUT).setDuration(150)
                detach(it)
            }
            fragment.enterTransition = Fade(Fade.MODE_IN).setDuration(150)
            add(R.id.overlay_container, fragment, tag)
            overlayTagStack.add(tag)
        }
    }

    /**
     * Do back action on overlay and return this back pressed event is captured
     */
    fun MainActivity.backOnOverlay(): Boolean {
        val manager = supportFragmentManager
        val fragmentToRemove = manager.findFragmentByTag(overlayTagStack.pop())?.also {
            val transition = Fade(Fade.MODE_OUT).setDuration(150)
            if (it is SearchFragment) {
                val binding = getActivityBinding()
                transition.addListener(SelfRemoveTransitionListener(
                        onStart = { binding.overlayBackground.visibility = GONE },
                        onEnd = { binding.overlayContainer.visibility = GONE }
                ))
            }
            it.exitTransition = transition
        }
        val fragmentToAttach = manager.findFragmentByTag(overlayTagStack.lastOrNull())
        manager.withTransaction {
            fragmentToRemove?.let { f -> remove(f) }
            fragmentToAttach?.let { f -> attach(f) }
        }
        return fragmentToRemove != null
    }

    private class SelfRemoveTransitionListener(
            private val onStart: ((Transition) -> Unit)? = null,
            private val onEnd: ((Transition) -> Unit)? = null
    ) : Transition.TransitionListener {

        override fun onTransitionEnd(p0: Transition) {
            onEnd?.invoke(p0)
            p0.removeListener(this)
        }

        override fun onTransitionStart(p0: Transition) {
            onStart?.invoke(p0)
        }

        override fun onTransitionResume(p0: Transition) {}
        override fun onTransitionPause(p0: Transition) {}
        override fun onTransitionCancel(p0: Transition) {}
    }
}