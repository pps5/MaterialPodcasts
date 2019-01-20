package io.github.pps5.materialpodcasts.di

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.github.pps5.materialpodcasts.R
import org.koin.dsl.module.module

val drawableModule = module {
    single<Drawable>("placeholder") { ColorDrawable(ContextCompat.getColor(get(), R.color.white)) }
}