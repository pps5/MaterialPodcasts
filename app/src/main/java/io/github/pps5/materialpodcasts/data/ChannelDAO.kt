package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.pps5.materialpodcasts.model.Channel

@Dao
interface ChannelDAO {

    @Query("SELECT * FROM channel WHERE collectionId = :collectionId")
    fun find(collectionId: Long): Channel?

    @Insert
    fun insert(channel: Channel)

}