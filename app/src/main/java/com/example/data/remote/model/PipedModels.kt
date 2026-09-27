package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PipedSearchResponse(
    @Json(name = "items") val items: List<PipedItem>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PipedItem(
    @Json(name = "title") val title: String = "",
    @Json(name = "type") val type: String = "stream",
    @Json(name = "url") val url: String = "",
    @Json(name = "uploaderName") val uploaderName: String? = null,
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "duration") val duration: Long? = 0
) {
    val videoId: String
        get() = url.substringAfter("/watch?v=", "")
}

@JsonClass(generateAdapter = true)
data class PipedStreamsResponse(
    @Json(name = "audioStreams") val audioStreams: List<PipedAudioStream>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PipedAudioStream(
    @Json(name = "url") val url: String = "",
    @Json(name = "bitrate") val bitrate: Int = 0,
    @Json(name = "format") val format: String = "M4A"
)
