package io.github.pps5.materialpodcasts.data

import io.github.pps5.materialpodcasts.model.ITunesResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("search?media=podcast")
    fun search(@Query("term") query: String): Deferred<ITunesResponse>

    @GET("lookup")
    fun lookup(@Query("id") id: Int): Deferred<ITunesResponse>
}