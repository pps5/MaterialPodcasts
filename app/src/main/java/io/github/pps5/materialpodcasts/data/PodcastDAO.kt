package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.pps5.materialpodcasts.model.Podcast

@Dao
interface PodcastDAO {

    @Query("SELECT * FROM podcast WHERE  collectionId = :collectionId")
    fun find(collectionId: Long): Podcast?

    @Query("SELECT * FROM podcast")
    fun findAll(): List<Podcast>

    @Insert
    fun insert(podcast: Podcast)
}