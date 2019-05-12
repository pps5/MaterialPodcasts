package io.github.pps5.materialpodcasts.extension

inline fun <T> T.applyIf(bool: Boolean, func: T.() -> Unit) = apply { if (bool) func() }

