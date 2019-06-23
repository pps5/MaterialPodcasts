package io.github.pps5.materialpodcasts.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.di.ACTIVITY_SCOPE
import io.github.pps5.materialpodcasts.model.Track
import io.github.pps5.materialpodcasts.service.MediaService
import io.github.pps5.materialpodcasts.service.MediaSubscriber
import io.github.pps5.materialpodcasts.view.adapter.PodcastDetailAdapter
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.COLLAPSED
import io.github.pps5.materialpodcasts.view.customview.SlidingPanel.PanelState.PEEK_HEIGHT_CHANGING
import io.github.pps5.materialpodcasts.view.viewmodel.MainViewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(),
    PodcastDetailAdapter.TrackSelectListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var topLevelNavigator: Navigator
    private val viewModel by viewModel<MainViewModel>()
    private var mediaSubscriber: MediaSubscriber? = null

    override fun onBackPressed() {
        when {
            binding.slidingUpPanel.panelState == PEEK_HEIGHT_CHANGING -> {
            }
            binding.slidingUpPanel.panelState != COLLAPSED ->
                binding.slidingUpPanel.panelState = COLLAPSED
            supportFragmentManager.backStackEntryCount == 1 -> topLevelNavigator.onBack()
            else -> super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bind()
    }

    private fun bind() {
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.slidingUpPanel.let {
            it.addOnSlideListener(binding.navigation)
            it.addOnSlideListener(viewModel)
            it.onChangeListener = binding.navigation
        }
        binding.playingView.setViewModel(this, viewModel)
    }

    override fun onStart() {
        super.onStart()
        getKoin().getOrCreateScope(ACTIVITY_SCOPE)
        topLevelNavigator = get { parametersOf(supportFragmentManager, binding.slidingUpPanel) }
        binding.navigation.setOnNavigationItemSelectedListener(topLevelNavigator)
        startService(Intent(this, MediaService::class.java))
        mediaSubscriber = MediaSubscriber(this).also { it.connect() }
        viewModel.onStart(mediaSubscriber!!)
    }

    override fun onStop() {
        super.onStop()
        getKoin().scopeRegistry.getScope(ACTIVITY_SCOPE)?.close()
        viewModel.onStop()
        mediaSubscriber?.disconnect()
    }

    override fun onSelect(track: Track) {
        Log.d(TAG, "onSelect: $track")
        mediaSubscriber?.play(track)
    }

}
