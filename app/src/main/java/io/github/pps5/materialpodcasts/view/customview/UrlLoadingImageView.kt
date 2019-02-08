package io.github.pps5.materialpodcasts.view.customview

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class UrlLoadingImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr), KoinComponent {

    companion object {
        private val TAG = UrlLoadingImageView::class.java.simpleName
    }

    private val placeholder: Drawable by inject("placeholder")
    var url: String? = null

    private fun get(url: String, callback: Callback) {
        Picasso.get().load(url)
                .placeholder(placeholder)
                .fit()
                .centerCrop()
                .into(this, callback)
    }

    fun getArtworkFromNetwork(baseUrl: String) {
        if (baseUrl.isEmpty()) {
            return
        }
        val urlGenerator = UrlGenerator(baseUrl)
        get(urlGenerator.next()!!, object : Callback {
            override fun onSuccess() {
                url = urlGenerator.lastGenerated
                Log.d(TAG, "success loading: $url")
            }

            override fun onError(e: java.lang.Exception?) {
                Log.d(TAG, "failed to load: ${e?.stackTrace}")
                urlGenerator.next()?.let { get(it, this) }
            }
        })
    }

    private class UrlGenerator(baseUrl: String) {

        companion object {
            private val RESOLUTIONS = arrayOf(
                    "600x600bb.jpg", "400x400bb.jpg", "100x100bb.jpg", "60x60bb.jpg", "30x30bb.jpg"
            )
        }

        private var nextIndex = 0
        private val urls = RESOLUTIONS.map { "$baseUrl/$it" }

        val lastGenerated: String?
            get() = if (nextIndex - 1 < 0) null else urls[nextIndex - 1]

        fun next() = if (nextIndex < urls.size) urls[nextIndex++] else null
    }

    @Suppress("unused")
    @BindingAdapter("imageUrl")
    fun UrlLoadingImageView.setImageUrl(url: String?) {
        url?.let { getArtworkFromNetwork(it) }
    }

}