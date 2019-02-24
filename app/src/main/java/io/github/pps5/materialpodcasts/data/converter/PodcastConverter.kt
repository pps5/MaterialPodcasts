package io.github.pps5.materialpodcasts.data.converter

import android.arch.persistence.room.TypeConverter
import com.squareup.moshi.Moshi
import io.github.pps5.materialpodcasts.model.Podcast
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PodcastConverter: KoinComponent {

    private val moshi: Moshi by inject()

    @TypeConverter
    fun toString(podcast: Podcast) = moshi.adapter(Podcast::class.java).toJson(podcast)!!

    @TypeConverter
    fun fromString(podcast: String) = moshi.adapter(Podcast::class.java).fromJson(podcast)
}