package io.github.pps5.materialpodcasts.extension

fun <T> ArrayList<T>.pop(): T? {
    return getOrNull(lastIndex)?.also { removeAt(lastIndex) }
}