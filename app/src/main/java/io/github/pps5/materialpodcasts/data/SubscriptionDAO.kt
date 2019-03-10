package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import io.github.pps5.materialpodcasts.model.Subscription
import io.github.pps5.materialpodcasts.model.Track

@Dao
interface SubscriptionDAO {

    @Query("SELECT * FROM subscription WHERE collectionId = :collectionId")
    fun find(collectionId: Long): Subscription?

    @Query("SELECT * FROM track WHERE collectionId = :collectionId")
    fun findTracks(collectionId: Long): List<Track>

    @Query("SELECT * FROM subscription")
    fun findAll(): List<Subscription>

    @Transaction
    @Query("DELETE FROM subscription WHERE collectionId = :collectionId")
    fun delete(collectionId: Long)

    @Insert
    fun insert(track: List<Track>)

    @Insert
    fun insert(subscription: Subscription)

    @Transaction
    fun insertSubscription(subscription: Subscription, tracks: List<Track>) {
        insert(subscription)
        insert(tracks)
    }

    @Transaction
    fun findWithTracks(collectionId: Long): Subscription? {
        val subscription = find(collectionId)
        if (subscription?.channel == null) {
            return null
        }
        val tracks = findTracks(collectionId)
        return if (tracks.isEmpty()) {
            null
        } else {
            subscription.also { s -> s.channel!!.tracks = tracks }
        }
    }
}
