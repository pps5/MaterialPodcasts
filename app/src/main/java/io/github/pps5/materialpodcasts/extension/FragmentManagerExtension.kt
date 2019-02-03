package io.github.pps5.materialpodcasts.extension

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun FragmentManager.withTransaction(action: FragmentTransaction.() -> Unit): Int {
    return beginTransaction().also {
        action(it)
    }.commit()
}

