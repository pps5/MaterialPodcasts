package io.github.pps5.materialpodcasts.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Podcast(

        @PrimaryKey var collectionId: Int = 0,
        var trackId: String = "",
        var trackCount: Int = 0,
        var releaseDate: String = "",

        var artistName: String = "",
        var collectionName: String = "",
        var trackName: String = "",

        var primaryGenreName: String = "",
        @Ignore var genreIds: Array<String> = arrayOf(),
        @Ignore var genres: Array<String> = arrayOf(),

        var collectionViewUrl: String = "",
        var feedUrl: String? = null,
        var trackViewUrl: String = "",

        var artworkUrl30: String = "",
        var artworkUrl60: String? = null,
        var artworkUrl100: String? = null,

        @Ignore var collectionPrice: Float = 0F,
        @Ignore var trackPrice: Float = 0F,
        @Ignore var trackRentalPrice: Int = 0,
        @Ignore var collectionHdPrice: Int = 0,
        @Ignore var trackHdPrice: Int = 0,
        @Ignore var trackHdRentalPrice: Int = 0
) {

    val artworkBaseUrl: String
        get() = artworkUrl30.substringBeforeLast('/')

    fun isFree(): Boolean {
        return arrayOf(collectionPrice, trackPrice, trackRentalPrice,
                collectionHdPrice, trackHdPrice, trackHdRentalPrice)
                .map { (it as Number).toFloat() }
                .all { it == 0F }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Podcast

        if (collectionId != other.collectionId) return false
        if (trackId != other.trackId) return false
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
        var result = collectionId
        result = 31 * result + trackId.hashCode()
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