package io.github.pps5.materialpodcasts.view.bindingadapter

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView

@BindingAdapter("app:time")
fun TextView.setTime(timeInMillis: Long) {
    val timeInSec = timeInMillis / 1000
    val min = timeInSec / 60
    val sec = timeInSec % 60
    this.text = String.format("%02d:%02d", min, sec)
}

@BindingAdapter("app:artwork")
fun ImageView.setArtwork(bitmap: Bitmap?) {
    bitmap?.let {
        this.setImageBitmap(it)
    }
}