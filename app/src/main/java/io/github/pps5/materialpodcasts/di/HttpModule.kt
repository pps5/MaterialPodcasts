package io.github.pps5.materialpodcasts.di

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import io.github.pps5.materialpodcasts.data.FeedsService
import io.github.pps5.materialpodcasts.data.ITunesService
import io.github.pps5.materialpodcasts.util.FeedXmlConverterFactory
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "HttpModule"
private const val ITUNES_API_ENDPOINT = "https://itunes.apple.com/"

val httpModule = module {

    single<OkHttpClient> {
        OkHttpClient().newBuilder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    Log.d(TAG, "Request to ${request.url()}")
                    return@addInterceptor chain.proceed(request)
                }
                .build()
    }

    single<Moshi> { Moshi.Builder().build() }

    single<ITunesService> {
        Retrofit.Builder().baseUrl(ITUNES_API_ENDPOINT)
                .client(get())
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(ITunesService::class.java)
    }

    single<FeedsService> {
        Retrofit.Builder()
                .baseUrl("https://example.com") // dummy url
                .client(get())
                .addConverterFactory(FeedXmlConverterFactory())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(FeedsService::class.java)
    }

}