package io.github.pps5.materialpodcasts.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "track",
    foreignKeys = [ForeignKey(
        entity = Subscription::class,
        parentColumns = arrayOf("collectionId"),
        childColumns = arrayOf("collectionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    var trackId: Int = 0,
    var collectionId: Int = 0,
    var guid: String? = null,
    var pubDate: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var description: String? = null,
    var link: String? = null,
    var duration: String? = null,
    var type: String? = null,
    var url: String? = null
)
