package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.pps5.materialpodcasts.model.Track

@Dao
interface TrackDAO {

    @Insert
    fun insertAll(track: List<Track>)

    @Query("SELECT * FROM track WHERE collectionId = :collectionId")
    fun find(collectionId: Long): List<Track>

    @Query("""SELECT * FROM track
        WHERE collectionId = :collectionId AND trackNumber = :trackNumber""")
    fun find(collectionId: Long, trackNumber: Int): Track?

}
