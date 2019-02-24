package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.pps5.materialpodcasts.data.converter.ChannelConverter
import io.github.pps5.materialpodcasts.data.converter.PodcastConverter
import io.github.pps5.materialpodcasts.model.*

private const val DB_VERSION = 1

@Database(
    entities = [Subscription::class, Track::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(ChannelConverter::class, PodcastConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSubscriptionDAO(): SubscriptionDAO
}