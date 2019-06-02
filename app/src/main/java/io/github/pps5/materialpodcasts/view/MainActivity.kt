package io.github.pps5.materialpodcasts.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.di.ACTIVITY_SCOPE
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.service.MediaService
import io.github.pps5.materialpodcasts.service.MediaSubscriber
import io.github.pps5.materialpodcasts.view.adapter.PodcastDetailAdapter
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.COLLAPSED
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.PEEK_HEIGHT_CHANGING
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(),
    PodcastDetailAdapter.TrackSelectListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topLevelNavigator: Navigator
    private val mediaSubscriber: MediaSubscriber by inject()

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

    override fun onStart() {
        super.onStart()
        getKoin().getOrCreateScope(ACTIVITY_SCOPE)
        topLevelNavigator = get { parametersOf(supportFragmentManager, binding.slidingUpPanel) }
        binding.navigation.setOnNavigationItemSelectedListener(topLevelNavigator)
        startService(Intent(this, MediaService::class.java))
        mediaSubscriber.connect()
    }

    override fun onStop() {
        super.onStop()
        getKoin().scopeRegistry.getScope(ACTIVITY_SCOPE)?.close()
        mediaSubscriber.disconnect()
    }

    override fun onSelect(track: Track) {
        mediaSubscriber.subscribe("${track.collectionId}/${track.trackNumber}")
    }

}
