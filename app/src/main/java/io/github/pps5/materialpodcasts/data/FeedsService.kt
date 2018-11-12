package io.github.pps5.materialpodcasts.data

import io.github.pps5.materialpodcasts.model.Channel
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface FeedsService {
    @GET
    fun getFeeds(@Url url: String): Deferred<Channel>
}