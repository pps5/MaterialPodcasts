package io.github.pps5.materialpodcasts.model

import java.util.*

data class Podcast(
        val wrapperType: String,
        val kind: String,

        val collectionId: Int,
        val trackId: String,
        val country: String,
        val trackCount: Int,
        val releaseDate: String,

        val artistName: String,
        val collectionName: String,
        val trackName: String,

        val primaryGenreName: String,
        val genreIds: Array<String>,
        val genres: Array<String>,

        val collectionViewUrl: String,
        val feedUrl: String?,
        val trackViewUrl: String,

        val artworkUrl30: String,
        val artworkUrl60: String?,
        val artworkUrl100: String?,

        val collectionPrice: Float,
        val trackPrice: Float,
        val trackRentalPrice: Int,
        val collectionHdPrice: Int,
        val trackHdPrice: Int,
        val trackHdRentalPrice: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Podcast

        if (wrapperType != other.wrapperType) return false
        if (kind != other.kind) return false
        if (collectionId != other.collectionId) return false
        if (trackId != other.trackId) return false
        if (country != other.country) return false
        if (trackCount != other.trackCount) return false
        if (releaseDate != other.releaseDate) return false
        if (artistName != other.artistName) return false
        if (collectionName != other.collectionName) return false
        if (trackName != other.trackName) return false
        if (primaryGenreName != other.primaryGenreName) return false
        if (!Arrays.equals(genreIds, other.genreIds)) return false
        if (!Arrays.equals(genres, other.genres)) return false
        if (collectionViewUrl != other.collectionViewUrl) return false
        if (feedUrl != other.feedUrl) return false
        if (trackViewUrl != other.trackViewUrl) return false
        if (artworkUrl30 != other.artworkUrl30) return false
        if (artworkUrl60 != other.artworkUrl60) return false
        if (artworkUrl100 != other.artworkUrl100) return false
        if (collectionPrice != other.collectionPrice) return false
        if (trackPrice != other.trackPrice) return false
        if (trackRentalPrice != other.trackRentalPrice) return false
        if (collectionHdPrice != other.collectionHdPrice) return false
        if (trackHdPrice != other.trackHdPrice) return false
        if (trackHdRentalPrice != other.trackHdRentalPrice) return false

        return true
    }

    override fun hashCode(): Int {
        var result = wrapperType.hashCode()
        result = 31 * result + kind.hashCode()
        result = 31 * result + collectionId
        result = 31 * result + trackId.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + trackCount
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + collectionName.hashCode()
        result = 31 * result + trackName.hashCode()
        result = 31 * result + primaryGenreName.hashCode()
        result = 31 * result + Arrays.hashCode(genreIds)
        result = 31 * result + Arrays.hashCode(genres)
        result = 31 * result + collectionViewUrl.hashCode()
        result = 31 * result + (feedUrl?.hashCode() ?: 0)
        result = 31 * result + trackViewUrl.hashCode()
        result = 31 * result + artworkUrl30.hashCode()
        result = 31 * result + (artworkUrl60?.hashCode() ?: 0)
        result = 31 * result + (artworkUrl100?.hashCode() ?: 0)
        result = 31 * result + collectionPrice.hashCode()
        result = 31 * result + trackPrice.hashCode()
        result = 31 * result + trackRentalPrice
        result = 31 * result + collectionHdPrice
        result = 31 * result + trackHdPrice
        result = 31 * result + trackHdRentalPrice
        return result
    }
}