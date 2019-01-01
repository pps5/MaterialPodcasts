package io.github.pps5.materialpodcasts.extension

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun FragmentManager.withTransaction(action: FragmentTransaction.(FragmentTransaction) -> FragmentTransaction): Int {
    return beginTransaction().also {
        it.action(it)
    }.commit()
}

