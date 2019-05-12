package io.github.pps5.materialpodcasts.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "channel",
    foreignKeys = [ForeignKey(
        entity = Subscription::class,
        parentColumns = arrayOf("collectionId"),
        childColumns = arrayOf("collectionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Channel(
    @PrimaryKey(autoGenerate = true)
    var channelId: Int = 0,
    var collectionId: Long = 0,
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var language: String? = null,
    @Transient var tracks: List<Track>? = null
) {

    fun setCollectionIdAndTrackNumber(collectionId: Long) {
        this.collectionId = collectionId
        tracks?.forEachIndexed { index, track ->
            track.collectionId = collectionId
            track.trackNumber = index
        }
    }
}