package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.model.Track

private const val DB_VERSION = 1

@Database(
    entities = [Subscription::class, Channel::class, Podcast::class, Track::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSubscriptionDAO(): SubscriptionDAO
    abstract fun getPodcastDAO(): PodcastDAO
    abstract fun getChannelDAO(): ChannelDAO
    abstract fun getTrackDAO(): TrackDAO
}