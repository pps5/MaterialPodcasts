package io.github.pps5.materialpodcasts.view.listener

import io.github.pps5.materialpodcasts.model.Podcast

interface PodcastSelectListener {
    fun onSelected(podcast: Podcast?)
}