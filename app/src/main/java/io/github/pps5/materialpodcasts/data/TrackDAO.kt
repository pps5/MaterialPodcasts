package io.github.pps5.materialpodcasts.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Track

@Dao
interface TrackDAO {

    @Insert
    fun insert(track: List<Track>)

    @Query("SELECT * FROM channel WHERE channelId = :channelId")
    fun find(channelId: Int): List<Channel>

}
