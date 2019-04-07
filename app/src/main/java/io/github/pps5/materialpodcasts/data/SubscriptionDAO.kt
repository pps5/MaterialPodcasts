package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Podcast
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.model.Track

@Dao
interface SubscriptionDAO {

    @Query("SELECT * FROM subscription WHERE collectionId = :collectionId")
    fun find(collectionId: Long): Subscription?

    @Query("SELECT * FROM subscription")
    fun findAll(): List<Subscription>

    @Transaction
    @Query("DELETE FROM subscription WHERE collectionId = :collectionId")
    fun delete(collectionId: Long)

    @Insert
    fun insert(subscription: Subscription)

    @Transaction
    fun insertSubscription(collectionId: Long, podcast: Podcast, channel: Channel, tracks: List<Track>) {

    }
}
