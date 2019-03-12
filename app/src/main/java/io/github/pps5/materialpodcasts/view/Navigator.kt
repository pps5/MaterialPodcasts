package io.github.pps5.materialpodcasts.view

import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.transition.Slide
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.MenuItem
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.fragment.PodcastDetailFragment
import io.github.pps5.materialpodcasts.view.fragment.SearchFragment
import io.github.pps5.materialpodcasts.view.fragment.SubscriptionFragment

class Navigator(
    private val fragmentManager: FragmentManager,
    private val interactionListener: InteractionListener
): BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val CONTAINER_ID = R.id.container
        private const val ACTION_DELAY: Long = 100L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val backStackSize
        get() = fragmentManager.backStackEntryCount

    private fun replaceFragment(fragment: Fragment, shouldAddToBackStack: Boolean = false) {
        fragmentManager.beginTransaction()
            .replace(CONTAINER_ID, fragment)
            .also { if (shouldAddToBackStack) it.addToBackStack(null) }
            .commit()
    }

    private fun popBackStack() = fragmentManager.popBackStack()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (!interactionListener.shouldHandleNavigationClick()) {
            return false
        }
        when (item.itemId) {
            R.id.navigation_home -> navigateToSearch()
            R.id.navigation_dashboard -> replaceFragment(SubscriptionFragment())
            R.id.navigation_notifications -> {
            }
        }
        return true
    }

    fun onBack() {
        handler.postDelayed(interactionListener::showBottomNavigation, ACTION_DELAY)
        popBackStack()
    }

    fun navigateToPodcastDetail(collectionId: Long, feedUrl: String, title: String,
                                artistName: String, artworkUrl: String) {
        val f = PodcastDetailFragment.newInstance(collectionId, feedUrl, title, artistName, artworkUrl)
        f.enterTransition = Slide(Gravity.BOTTOM).setDuration(100)
        replaceFragment(f, shouldAddToBackStack = true)
        if (backStackSize == 0) {
            handler.postDelayed(interactionListener::hideBottomNavigation, ACTION_DELAY)
        }
    }

    fun navigateToSearch() {
        replaceFragment(
            fragment = SearchFragment().also {
                it.enterTransition = Slide(Gravity.BOTTOM).setDuration(100)
            },
            shouldAddToBackStack = true
        )
        handler.postDelayed(interactionListener::hideBottomNavigation, ACTION_DELAY)
    }

    interface InteractionListener {
        fun showBottomNavigation()
        fun hideBottomNavigation()
        fun shouldHandleNavigationClick(): Boolean
    }
}
