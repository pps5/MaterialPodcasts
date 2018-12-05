package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.view.customview.NowPlayingView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val callbackMediator = NowPlayingView.CallbackMediator()
        binding.nowplaying.bottomSheet.setCallbackMediator(callbackMediator)
        binding.navigation.let {
            it.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            it.setCallbackMediator(callbackMediator)
        }
    }
}
