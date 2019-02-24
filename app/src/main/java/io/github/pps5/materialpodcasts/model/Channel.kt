package io.github.pps5.materialpodcasts.model

data class Channel(
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var language: String? = null,
    @Transient var tracks: List<Track>? = null
)