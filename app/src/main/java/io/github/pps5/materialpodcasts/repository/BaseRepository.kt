package io.github.pps5.materialpodcasts.repository

import kotlinx.coroutines.Job

abstract class BaseRepository {

    protected var job: Job? = null

    protected fun cancel() {
        if (job?.isCancelled == false) {
            job?.cancel()
        }
    }
}