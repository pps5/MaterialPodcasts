package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import io.github.pps5.materialpodcasts.model.*

@Dao
interface SubscriptionDAO {

    @Query("SELECT * FROM subscription WHERE collectionId = :collectionId")
    fun find(collectionId: Int): Subscription?

    @Query("SELECT * FROM subscription")
    fun findAll(): List<Subscription>

    @Transaction
    @Query("DELETE FROM subscription WHERE collectionId = :collectionId")
    fun delete(collectionId: Int)

    @Insert
    fun insert(track: List<Track>)

    @Insert
    fun insert(subscription: Subscription)

    @Transaction
    fun insertSubscription(subscription: Subscription, tracks: List<Track>) {
        insert(subscription)
        insert(tracks)
    }

}
