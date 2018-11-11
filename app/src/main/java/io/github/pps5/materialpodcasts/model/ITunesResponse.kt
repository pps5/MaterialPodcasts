package io.github.pps5.materialpodcasts.model

import java.util.*

data class ITunesResponse (
        val resultCount: Int,
        val results: Array<Podcast>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ITunesResponse

        if (resultCount != other.resultCount) return false
        if (!Arrays.equals(results, other.results)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resultCount
        result = 31 * result + Arrays.hashCode(results)
        return result
    }
}