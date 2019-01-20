package io.github.pps5.materialpodcasts.model

data class Item(
        var guid: String? = null,
        var pubDate: String? = null,
        var title: String? = null,
        var enclosure: Enclosure? = null,
        var description: String? = null,
        var link: String? = null,
        var subtitle: String? = null,
        var duration: String? = null
)