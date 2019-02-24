package io.github.pps5.materialpodcasts.repository

import io.github.pps5.materialpodcasts.vo.Resource

abstract class CacheableRepository<Request, Response>: BaseRepository() {
    abstract fun shouldFetchFromNetwork(): Boolean
    abstract suspend fun fetchFromNetwork(request: Request): Resource<Response>
    abstract suspend fun fetchFromDb(request: Request): Resource<Response>
}