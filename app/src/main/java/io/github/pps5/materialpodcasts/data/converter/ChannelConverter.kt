package io.github.pps5.materialpodcasts.data.converter

import android.arch.persistence.room.TypeConverter
import com.squareup.moshi.Moshi
import io.github.pps5.materialpodcasts.model.Channel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ChannelConverter : KoinComponent {

    private val moshi: Moshi by inject()

    @TypeConverter
    fun toString(channel: Channel) = moshi.adapter(Channel::class.java).toJson(channel)!!

    @TypeConverter
    fun fromString(channel: String) = moshi.adapter(Channel::class.java).fromJson(channel)
}
