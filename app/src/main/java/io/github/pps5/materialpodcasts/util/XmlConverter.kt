package io.github.pps5.materialpodcasts.util

import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Track
import okhttp3.ResponseBody
import org.w3c.dom.Node
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.xml.parsers.DocumentBuilderFactory

class FeedXmlConverterFactory : Converter.Factory() {

    companion object {
        private const val TAG_CHANNEL = "channel"
        private const val TAG_ITEM = "item"
        private const val TAG_TITLE = "title"
        private const val TAG_SUBTITLE = "itunes:subtitle"
        private const val TAG_DURATION = "itunes:duration"
        private const val TAG_ENCLOSURE = "enclosure"
        private const val TAG_GUID = "guid"
        private const val TAG_LINK = "link"
        private const val TAG_DESCRIPTION = "description"
        private const val TAG_PUBDATE = "pubDate"
        private const val TAG_LANGUAGE = "language"
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return XmlConverter()
    }

    class XmlConverter : Converter<ResponseBody, Channel> {

        override fun convert(value: ResponseBody): Channel {
            val dom = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(value.byteStream())
                .documentElement
            val channel = dom.getElementsByTagName(TAG_CHANNEL).item(0) ?: return Channel()
            val channelChildren = channel.childNodes
            val result = Channel()
            val tracks = ArrayList<Track>(channelChildren.length)
            for (i in 0 until channelChildren.length) {
                val child = channelChildren.item(i)
                when (child.nodeName) {
                    TAG_TITLE -> result.title = child.textContent
                    TAG_DESCRIPTION -> result.description = child.textContent
                    TAG_LINK -> result.link = child.textContent
                    TAG_LANGUAGE -> result.language = child.textContent
                    TAG_ITEM -> tracks.add(child.parseItem())
                }
            }
            return result.also { it.tracks = tracks }
        }

        private fun Node.parseItem(): Track {
            val itemChildNodes = this.childNodes
            val track = Track()
            for (i in 0 until itemChildNodes.length) {
                val child = itemChildNodes.item(i)
                when (child.nodeName) {
                    TAG_TITLE -> track.title = child.textContent
                    TAG_SUBTITLE -> track.subtitle = child.textContent
                    TAG_DURATION -> track.duration = child.textContent
                    TAG_ENCLOSURE -> child.let {
                        track.type = child.attributes?.getNamedItem("type")?.nodeValue
                        track.url = child.attributes?.getNamedItem("url")?.nodeValue
                    }
                    TAG_GUID -> track.guid = child.textContent
                    TAG_LINK -> track.link = child.textContent
                    TAG_DESCRIPTION -> track.description = child.textContent
                    TAG_PUBDATE -> track.pubDate = child.textContent
                }
            }
            return track
        }
    }
}


