package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.pps5.materialpodcasts.model.Podcast

@Dao
interface PodcastDAO {

    @Query("SELECT * FROM podcast WHERE collectionId = :collectionId")
    fun find(collectionId: Int): Podcast?

    @Query("SELECT * FROM podcast")
    fun findAll(): List<Podcast>

    @Insert
    fun insert(podcast: Podcast)

    @Delete
    fun delete(podcast: Podcast)

    @Query("DELETE FROM podcast WHERE collectionId = :collectionId")
    fun delete(collectionId: Int)
}