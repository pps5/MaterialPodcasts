package io.github.pps5.materialpodcasts.model

data class Podcast(
    var trackCount: Int = 0,
    var artistName: String = "",
    var collectionName: String = "",
    var trackName: String = "",
    var primaryGenreName: String = "",

    var collectionViewUrl: String = "",
    var feedUrl: String? = null,
    var trackViewUrl: String = "",

    var artworkUrl30: String = "",
    var artworkUrl60: String? = null,
    var artworkUrl100: String? = null,

    @Transient var collectionPrice: Float = 0F,
    @Transient var trackPrice: Float = 0F,
    @Transient var trackRentalPrice: Int = 0,
    @Transient var collectionHdPrice: Int = 0,
    @Transient var trackHdPrice: Int = 0,
    @Transient var trackHdRentalPrice: Int = 0
) {

    val artworkBaseUrl: String
        get() = artworkUrl30.substringBeforeLast('/')

    fun isFree(): Boolean {
        return arrayOf(collectionPrice, trackPrice, trackRentalPrice,
            collectionHdPrice, trackHdPrice, trackHdRentalPrice)
            .map { (it as Number).toFloat() }
            .all { it == 0F }
    }
}