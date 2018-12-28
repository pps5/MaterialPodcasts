package io.github.pps5.materialpodcasts.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.databinding.ActivityMainBinding
import io.github.pps5.materialpodcasts.view.viewmodel.BottomSheetViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sheetCallbackMediator: SheetCallbackMediator by inject()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.slidingUpPanel.addPanelSlideListener(sheetCallbackMediator.slideListener)
        binding.nowPlayingSheet.initialize(binding.slidingUpPanel, BottomSheetViewModel())
    }
}
