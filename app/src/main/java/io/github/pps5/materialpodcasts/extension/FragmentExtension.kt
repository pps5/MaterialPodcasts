package io.github.pps5.materialpodcasts.extension

import android.support.v4.app.Fragment

inline fun <reified T> Fragment.args(key: String): Lazy<T> {
    return lazy { arguments!!.get(key) as T }
}