package io.github.pps5.materialpodcasts.model

data class Channel(
        var title: String? = null,
        var description: String? = null,
        var items: List<Item>? = null,
        var link: String? = null,
        var language: String? = null
)