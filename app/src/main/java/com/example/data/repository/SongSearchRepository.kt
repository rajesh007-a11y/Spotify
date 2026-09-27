package com.example.data.repository

import com.example.data.model.Song
import com.example.data.remote.JioSaavnApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

data class ArtistAwareSearchResult(
    val isArtistMode: Boolean,
    val artistName: String?,
    val songs: List<Song>
)

data class TrackResult(
    val id: String,
    val title: String,
    val artist: String,
    val albumTitle: String,
    val albumArtUrl: String?,
    val streamUrl320kbps: String,
    val durationMs: Long
)

class SongSearchRepository(
    private val apiService: JioSaavnApiService = JioSaavnApiService.create(),
    private val pipedApiService: com.example.data.remote.PipedApiService = com.example.data.remote.PipedApiService.create()
) {

    /**
     * Searches JioSaavn API and returns parsed TrackResults with direct 320kbps audio URLs.
     */
    suspend fun searchTracks(query: String): Result<List<TrackResult>> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext Result.success(emptyList())

        try {
            val response = apiService.searchSongs(query = query)
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("JioSaavn API error: HTTP ${response.code()} ${response.message()}")
                )
            }

            val rawSongs = response.body()?.data?.results.orEmpty()
            val tracks = rawSongs.mapNotNull { item ->
                val streamUrl = item.get320KbpsStreamingUrl() ?: return@mapNotNull null

                TrackResult(
                    id = item.id,
                    title = item.title
                        .replace("&quot;", "\"")
                        .replace("&#039;", "'")
                        .replace("&amp;", "&"),
                    artist = item.artists?.primary?.mapNotNull { it.name }?.joinToString(", ")
                        ?.replace("&amp;", "&")
                        ?.replace("&#039;", "'")
                        ?.ifBlank { "Unknown Artist" } ?: "Unknown Artist",
                    albumTitle = item.album?.name ?: "",
                    albumArtUrl = item.getHighResAlbumArt(),
                    streamUrl320kbps = streamUrl,
                    durationMs = (item.durationSeconds ?: 0) * 1000L
                )
            }

            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resolves the top direct 320kbps streaming URL for a given song query.
     */
    suspend fun getTopStreamUrl(songQuery: String): String? = withContext(Dispatchers.IO) {
        searchTracks(songQuery).getOrNull()?.firstOrNull()?.streamUrl320kbps
    }

    /**
     * Helper that returns full domain Song models ready for the player & UI.
     */
    suspend fun searchDomainSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = apiService.searchSongs(query = query)
            if (!response.isSuccessful) return@withContext emptyList()

            response.body()?.data?.results.orEmpty()
                .mapNotNull { it.toDomainSong() }
                .applyAggressiveQualityFilter(query)
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Artist/band-aware search. When the query matches an artist or band name
     * (e.g. "Fossils", "Fakira", "Atif Aslam", "Arijit Singh"), this returns that
     * artist's songs as a grouped "playlist" instead of a generic keyword match.
     *
     * Strategy: fetch a larger result set from the songs search endpoint (which
     * already fuzzy-matches on artist name), then check how many results have an
     * artist field that actually contains the query. If a meaningful chunk does,
     * treat this as "artist mode": return only that artist's songs (deduped,
     * quality-filtered) as the playlist. Otherwise fall back to the normal mixed
     * search results.
     */
    suspend fun searchArtistAwareSongs(query: String): ArtistAwareSearchResult = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext ArtistAwareSearchResult(false, null, emptyList())
        try {
            // Pull two pages with a higher limit so we have enough songs to detect
            // a genuine artist match instead of just the first few generic hits.
            val page1Deferred = async { apiService.searchSongs(query = query, page = 1, limit = 40) }
            val page2Deferred = async { apiService.searchSongs(query = query, page = 2, limit = 40) }
            val page1 = page1Deferred.await()
            val page2 = page2Deferred.await()

            val rawResults = (page1.body()?.data?.results.orEmpty() + page2.body()?.data?.results.orEmpty())
            val allSongs = rawResults.mapNotNull { it.toDomainSong() }.distinctBy { it.id }

            val queryNormalized = query.trim().lowercase()
            val artistMatches = allSongs.filter { song ->
                song.artist.lowercase().split(",").any { name ->
                    val n = name.trim()
                    n == queryNormalized || n.contains(queryNormalized) || queryNormalized.contains(n)
                }
            }

            // If a solid chunk of results are genuinely by that artist/band, show
            // it as a dedicated artist playlist (skip the aggressive quality
            // filter here since indie/regional bands often get filtered out by
            // the spam-keyword/duration heuristics meant for generic search spam).
            if (artistMatches.size >= 3) {
                val artistDisplayName = artistMatches
                    .flatMap { it.artist.split(",") }
                    .map { it.trim() }
                    .firstOrNull { it.lowercase().contains(queryNormalized) || queryNormalized.contains(it.lowercase()) }
                    ?: query

                return@withContext ArtistAwareSearchResult(
                    isArtistMode = true,
                    artistName = artistDisplayName,
                    songs = artistMatches.distinctBy { it.id }
                )
            }

            val finalSaavnSongs = allSongs.applyAggressiveQualityFilter(query)
            
            val finalCombinedSongs = if (finalSaavnSongs.size < 10) {
                try {
                    val pipedResponse = pipedApiService.searchYouTube(query)
                    if (pipedResponse.isSuccessful) {
                        val items = pipedResponse.body()?.items?.filter { it.type == "stream" }.orEmpty().take(15)
                        
                        val fetchedSongs = items.map { item ->
                            async {
                                val videoId = item.videoId
                                if (videoId.isBlank()) return@async null
                                try {
                                    val streamRes = pipedApiService.getStreams(videoId)
                                    if (!streamRes.isSuccessful) return@async null
                                    
                                    val streamUrl = streamRes.body()?.audioStreams?.firstOrNull()?.url ?: return@async null
                                    
                                    Song(
                                        id = "yt_$videoId",
                                        title = item.title,
                                        artist = item.uploaderName ?: "Unknown",
                                        album = "YouTube",
                                        durationMs = (item.duration ?: 210) * 1000L,
                                        albumArtUrl = item.thumbnail ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                                        audioUrl = streamUrl,
                                        genre = "YouTube",
                                        energy = 0.75f,
                                        valence = 0.70f,
                                        bpm = 110,
                                        lyrics = "[00:00] Streaming via YouTube Fallback",
                                        language = "English"
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }.awaitAll().filterNotNull()

                        val newSongs = fetchedSongs.filter { ytSong ->
                            val ytTitleNorm = ytSong.title.lowercase()
                            val ytArtistNorm = ytSong.artist.lowercase()
                            !finalSaavnSongs.any { s -> 
                                s.title.lowercase() == ytTitleNorm && s.artist.lowercase() == ytArtistNorm
                            }
                        }

                        finalSaavnSongs + newSongs
                    } else {
                        finalSaavnSongs
                    }
                } catch (e: Exception) {
                    finalSaavnSongs
                }
            } else {
                finalSaavnSongs
            }

            ArtistAwareSearchResult(false, null, finalCombinedSongs)
        } catch (_: Exception) {
            ArtistAwareSearchResult(false, null, emptyList())
        }
    }

    private fun normalizedTitleKey(song: Song): String {
        return song.title.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim().lowercase()
    }

    suspend fun getSameArtistTopSongs(
        song: Song,
        excludeIds: Set<String>,
        excludeTitleKeys: Set<String>,
        limit: Int = 10
    ): List<Song> = withContext(Dispatchers.IO) {
        try {
            val singerNames = song.artist.split(",", "&", "/").map { it.trim() }.filter { it.isNotBlank() && !it.equals("Various Artists", ignoreCase = true) }
            val composerNames = song.composers.split(",", "&", "/").map { it.trim() }.filter { it.isNotBlank() && !it.equals("Various Artists", ignoreCase = true) }
            val namesToTry = (singerNames + composerNames).distinct()
            
            val results = mutableListOf<Song>()
            
            for (name in namesToTry) {
                if (results.size >= limit) break
                try {
                    val response = apiService.searchSongs(query = name, limit = 20)
                    if (response.isSuccessful) {
                        val candidates = response.body()?.data?.results.orEmpty().mapNotNull { it.toDomainSong() }
                        for (candidate in candidates) {
                            if (results.size >= limit) break
                            
                            val titleKey = normalizedTitleKey(candidate)
                            val isMatch = candidate.artist.contains(name, ignoreCase = true) || candidate.composers.contains(name, ignoreCase = true)
                            
                            if (isMatch && candidate.id != song.id && !excludeIds.contains(candidate.id) && !excludeTitleKeys.contains(titleKey)) {
                                val alreadyAdded = results.any { it.id == candidate.id || normalizedTitleKey(it) == titleKey }
                                if (!alreadyAdded) {
                                    results.add(candidate)
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore per-name network failure
                }
            }
            results
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Fetches similar songs for a given JioSaavn song ID.
     */
    suspend fun getSimilarDomainSongs(songId: String): List<Song> = withContext(Dispatchers.IO) {
        if (songId.isBlank()) return@withContext emptyList()
        try {
            // Strip "saavn_" prefix if present
            val cleanId = songId.removePrefix("saavn_")
            val response = apiService.getSimilarSongs(id = cleanId)
            if (!response.isSuccessful) return@withContext emptyList()

            // Shuffle the results to avoid echo chamber loop, and aggressively filter fakes
            response.body()?.data.orEmpty()
                .mapNotNull { it.toDomainSong() }
                .applyAggressiveQualityFilter()
                .shuffled()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Fetches YouTube recommendations and translates them to JioSaavn songs with strict blacklist checking.
     */
    suspend fun getYouTubeRecommendations(
        currentTitle: String, 
        currentArtist: String,
        blacklistTitles: Set<String>
    ): List<Song> = withContext(Dispatchers.IO) {
        try {
            val pipedResponse = pipedApiService.searchYouTube("$currentTitle $currentArtist")
            if (!pipedResponse.isSuccessful) return@withContext emptyList()

            // 1. Fetch at least 15-20 results and shuffle them
            val topItems = pipedResponse.body()?.items?.filter { it.type == "stream" }?.take(20)?.shuffled().orEmpty()
            
            val recommendedSongs = mutableListOf<Song>()
            val localBlacklist = blacklistTitles.toMutableSet()
            for (item in topItems) {
                // 3. Pick 2-3 completely unique tracks
                if (recommendedSongs.size >= 3) break
                
                val cleanPipedTitle = item.title.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim().lowercase()
                
                // 2. History Blacklist checking
                if (localBlacklist.any { cleanPipedTitle.contains(it) || it.contains(cleanPipedTitle) }) {
                    continue
                }

                // Search JioSaavn silently for the recommended title
                val saavnResults = searchDomainSongs(item.title)
                
                val validSong = saavnResults.firstOrNull { saavnSong ->
                    val cleanSaavnTitle = saavnSong.title.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim().lowercase()
                    !localBlacklist.contains(cleanSaavnTitle)
                }
                
                validSong?.let { 
                    recommendedSongs.add(it)
                    localBlacklist.add(it.title.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim().lowercase())
                }
            }
            recommendedSongs
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * AGGRESSIVE QUALITY FILTER
     * Drops fake tracks, generic EDM loops, and remixes based on keywords, duration, and album names.
     */
    private fun List<Song>.applyAggressiveQualityFilter(query: String = ""): List<Song> {
        val queryWords = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        val allSpamKeywords = listOf(
            "remix", "dj", "instrumental", "cover", "lofi", "slowed", "reverb", "8d", 
            "mashup", "bgm", "karaoke", "version", "mix", "soundify", "trance", "techno", "bass boosted"
        )
        
        // If the user explicitly searched for a spam keyword, don't filter it out
        val spamKeywords = allSpamKeywords.filter { spam ->
            queryWords.none { q -> q.contains(spam) || spam.contains(q) }
        }
        
        val spamAlbums = listOf("hot hits", "happy vibes")

        return this.filter { song ->
            val title = song.title.lowercase()
            val artist = song.artist.lowercase()
            val album = song.album.lowercase()

            // 1. RUTHLESS KEYWORD BLACKLIST
            val hasSpamKeyword = spamKeywords.any { keyword ->
                title.contains(keyword) || artist.contains(keyword) || album.contains(keyword)
            }
            if (hasSpamKeyword) return@filter false

            // 2. DURATION FILTER (Between 60s and 600s)
            val durationSeconds = song.durationMs / 1000
            if (durationSeconds < 60 || durationSeconds > 600) return@filter false

            // 3. SPAM ALBUM CHECK
            val isSpamAlbum = spamAlbums.any { album.contains(it) }
            if (isSpamAlbum) return@filter false

            true
        }
    }
}
