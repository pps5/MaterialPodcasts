package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.view.customview.NowPlayingView
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val callbackMediator = NowPlayingView.CallbackMediator()
        binding.navigation.let {
            it.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            it.setCallbackMediator(callbackMediator)
        }
        initializeBottomSheet(callbackMediator)
    }

    private fun initializeBottomSheet(callbackMediator: NowPlayingView.CallbackMediator) {
        val bottomSheetViewModel = BottomSheetViewModel()
        binding.nowplaying.setLifecycleOwner(this)
        binding.nowplaying.bottomSheet.initialize(callbackMediator = callbackMediator,
                binding = binding.nowplaying, viewModel = bottomSheetViewModel)
    }
}
