package io.github.pps5.materialpodcasts.vo

sealed class Resource<T> {

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(value: T) = Success(value)
        fun <T> error(throwable: Throwable, defaultValue: T? = null) = Error(throwable, defaultValue)
    }

    class Loading<T>: Resource<T>()
    data class Success<T>(val value: T): Resource<T>()
    data class Error<T>(val throwable: Throwable, val defaultValue: T?): Resource<T>()
}



