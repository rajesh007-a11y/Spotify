package com.example.data.remote.model

import com.example.data.model.Song
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SaavnSearchResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "data") val data: SaavnSearchData? = null
)

@JsonClass(generateAdapter = true)
data class SaavnSuggestionsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "data") val data: List<SaavnSongItem>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class SaavnSearchData(
    @Json(name = "total") val total: Int = 0,
    @Json(name = "start") val start: Int = 0,
    @Json(name = "results") val results: List<SaavnSongItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SaavnSongItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val title: String,
    @Json(name = "artists") val artists: SaavnArtistsObj? = null,
    @Json(name = "album") val album: SaavnAlbumInfo? = null,
    @Json(name = "year") val year: String? = null,
    @Json(name = "duration") val durationSeconds: Int? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "image") val imageList: List<SaavnMediaPayload> = emptyList(),
    @Json(name = "downloadUrl") val downloadUrlList: List<SaavnMediaPayload> = emptyList()
) {
    /**
     * Resolves the highest resolution album art (prefers 500x500).
     */
    fun getHighResAlbumArt(): String? {
        return imageList.find { it.quality?.equals("500x500", ignoreCase = true) == true }?.url
            ?: imageList.lastOrNull()?.url
    }

    /**
     * Resolves the highest quality direct stream link (prefers 320kbps, then 160kbps).
     */
    fun get320KbpsStreamingUrl(): String? {
        return downloadUrlList.find { it.quality?.equals("320kbps", ignoreCase = true) == true }?.url
            ?: downloadUrlList.find { it.quality?.equals("160kbps", ignoreCase = true) == true }?.url
            ?: downloadUrlList.find { it.quality?.equals("96kbps", ignoreCase = true) == true }?.url
            ?: downloadUrlList.lastOrNull()?.url
    }

    /**
     * Helper to map this Saavn API response into the application's domain Song entity.
     */
    fun toDomainSong(): Song? {
        val streamUrl = get320KbpsStreamingUrl() ?: return null
        val cleanTitle = title
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&amp;", "&")

        val primaryArtistNames = artists?.primary?.mapNotNull { it.name }?.joinToString(", ") ?: "Various Artists"
        val cleanArtist = primaryArtistNames
            .replace("&amp;", "&")
            .replace("&#039;", "'")
            .ifBlank { "Various Artists" }

        val cleanAlbum = album?.name
            ?.replace("&quot;", "\"")
            ?.replace("&amp;", "&")
            ?.ifBlank { "Single" } ?: "Single"

        return Song(
            id = "saavn_$id",
            title = cleanTitle,
            artist = cleanArtist,
            album = cleanAlbum,
            durationMs = (durationSeconds ?: 210) * 1000L,
            albumArtUrl = getHighResAlbumArt()
                ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            audioUrl = streamUrl,
            genre = language?.replaceFirstChar { it.uppercase() } ?: "Pop",
            energy = 0.75f,
            valence = 0.70f,
            bpm = 110,
            lyrics = "[00:00] Streaming via JioSaavn 320kbps Lossless Audio",
            language = language?.replaceFirstChar { it.uppercase() } ?: "Hindi"
        )
    }
}

@JsonClass(generateAdapter = true)
data class SaavnAlbumInfo(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class SaavnMediaPayload(
    @Json(name = "quality") val quality: String? = null,
    @Json(name = "url") val url: String? = null
)

@JsonClass(generateAdapter = true)
data class SaavnArtistsObj(
    @Json(name = "primary") val primary: List<SaavnArtistInfo> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SaavnArtistInfo(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null
)
