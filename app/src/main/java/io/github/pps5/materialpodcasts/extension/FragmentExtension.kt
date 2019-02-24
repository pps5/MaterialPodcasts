package io.github.pps5.materialpodcasts.extension

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup

inline fun <reified T> Fragment.args(key: String): Lazy<T> {
    return lazy { arguments!!.get(key) as T }
}

fun <T: ViewDataBinding> LayoutInflater.inflateBinding(
        @LayoutRes layoutId: Int,
        container: ViewGroup?
) = DataBindingUtil.inflate<T>(this, layoutId, container, false)!!
