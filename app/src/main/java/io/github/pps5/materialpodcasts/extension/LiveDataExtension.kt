package io.github.pps5.materialpodcasts.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}

fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, func)
}

fun <X> LiveData<X>.observe(owner: LifecycleOwner, func: (X?) -> Unit) {
    observe(owner, Observer { func(it) })
}

fun <X> LiveData<X>.observeNonNull(owner: LifecycleOwner, func: (X) -> Unit) {
    observe(owner, Observer { if (it != null) func(it) })
}