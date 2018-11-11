package io.github.pps5.materialpodcasts.util

import io.github.pps5.materialpodcasts.model.Channel
import io.github.pps5.materialpodcasts.model.Enclosure
import io.github.pps5.materialpodcasts.model.Item
import junit.framework.Assert.assertEquals
import okhttp3.ResponseBody
import org.junit.Test


class FeedXmlConverterFactoryTest {

    @Test
    fun responseBodyConverter() {
        val converter = FeedXmlConverterFactory.XmlConverter()
        val xml = javaClass.classLoader.getResource("podcast_response.xml").readText()
        val responseBody = ResponseBody.create(null, xml)
        val channel = converter.convert(responseBody)
        assertEquals(expectedChannel, channel)
    }

    private val expectedChannel = Channel(
            title = "Awesome Podcast!",
            link = "https://example.com/feeds/",
            language = "en-US",
            description = "This is awesome podcast!",
            items = listOf(
                    Item(title = "First track",
                            subtitle = "First track subtitle",
                            description = "This is first track of Awesome Podcast!",
                            guid = "https://example.com/feeds?track=1",
                            link = "https://example.com/feeds?track=1",
                            enclosure = Enclosure(
                                    url = "https://example.com/feeds?track=1",
                                    length = "10000",
                                    type = "audio/mpeg"),
                            pubDate = "Fri, 26 Jun 2000 20:00:00 0000"),
                    Item(title = "Second track",
                            subtitle = "Second track subtitle",
                            description = "This is second track of Awesome Podcast!",
                            guid = "https://example.com/feeds?track=2",
                            link = "https://example.com/feeds?track=2",
                            enclosure = Enclosure(
                                    url = "https://example.com/feeds?track=2",
                                    length = "20000",
                                    type = "audio/mpeg"),
                            pubDate = "Fri, 26 Jun 2001 20:00:00 0000")
            )
    )
}
