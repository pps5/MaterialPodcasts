package io.github.pps5.materialpodcasts.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import io.github.pps5.materialpodcasts.data.converter.ChannelConverter
import io.github.pps5.materialpodcasts.data.converter.PodcastConverter

@Entity(tableName = "subscription")
data class Subscription(
    @PrimaryKey var collectionId: Int,

    @TypeConverters(PodcastConverter::class)
    var podcast: Podcast?,

    @TypeConverters(ChannelConverter::class)
    var channel: Channel?
)