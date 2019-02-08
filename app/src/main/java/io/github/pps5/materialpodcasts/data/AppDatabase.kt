package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.pps5.materialpodcasts.model.Podcast

private const val DB_VERSION = 1

@Database(entities = [Podcast::class], version = DB_VERSION, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getSubscriptionDAO(): PodcastDAO
}