package com.example.data.repository

import android.content.Context
import com.example.data.local.CatalogData
import com.example.data.local.ListeningDao
import com.example.data.local.ListeningEventEntity
import com.example.data.local.PlaylistDao
import com.example.data.local.PlaylistEntity
import com.example.data.local.SongDao
import com.example.data.local.SongEntity
import com.example.data.model.DjVibe
import com.example.data.model.GenreStat
import com.example.data.model.Song
import com.example.data.model.TasteProfile
import com.example.data.remote.GeminiDjService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MusicRepository(
    private val songDao: SongDao,
    private val listeningDao: ListeningDao,
    private val playlistDao: PlaylistDao,
    private val geminiDjService: GeminiDjService = GeminiDjService()
) {
    val allSongs: Flow<List<Song>> = songDao.getAllSongsFlow().map { entities ->
        entities.map { it.toSong() }
    }

    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongsFlow().map { entities ->
        entities.map { it.toSong() }
    }

    val downloadedSongs: Flow<List<Song>> = songDao.getDownloadedSongsFlow().map { entities ->
        entities.map { it.toSong() }
    }

    val recentlyPlayed: Flow<List<Song>> = songDao.getRecentlyPlayedFlow().map { entities ->
        entities.map { it.toSong() }
    }

    val heavyRotation: Flow<List<Song>> = songDao.getHeavyRotationFlow().map { entities ->
        entities.map { it.toSong() }
    }

    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylistsFlow()

    suspend fun getSongById(songId: String): Song? {
        return songDao.getSongById(songId)?.toSong()
    }

    suspend fun toggleFavorite(song: Song) = withContext(Dispatchers.IO) {
        songDao.insertSongIfNotExists(SongEntity.fromSong(song))
        val newFav = !song.isFavorite
        songDao.updateFavorite(song.id, newFav)
    }

    suspend fun recordListeningEvent(
        song: Song,
        durationListenedMs: Long,
        wasCompleted: Boolean,
        wasSkipped: Boolean
    ) = withContext(Dispatchers.IO) {
        songDao.insertSongIfNotExists(SongEntity.fromSong(song))
        val now = System.currentTimeMillis()
        songDao.recordPlay(song.id, now)

        val timeOfDay = when (SimpleDateFormat("HH", Locale.getDefault()).format(Date(now)).toIntOrNull() ?: 12) {
            in 5..11 -> "Morning"
            in 12..16 -> "Afternoon"
            in 17..21 -> "Evening"
            else -> "Late Night"
        }

        listeningDao.recordEvent(
            ListeningEventEntity(
                songId = song.id,
                songTitle = song.title,
                artistName = song.artist,
                genre = song.genre,
                energy = song.energy,
                valence = song.valence,
                listenedDurationMs = durationListenedMs,
                wasCompleted = wasCompleted,
                wasSkipped = wasSkipped,
                wasLiked = song.isFavorite,
                timestamp = now,
                timeOfDay = timeOfDay
            )
        )
    }

    suspend fun downloadSong(song: Song, context: Context): Boolean = withContext(Dispatchers.IO) {
        songDao.insertSongIfNotExists(SongEntity.fromSong(song))
        try {
            val downloadDir = File(context.filesDir, "soundify_offline_audio")
            if (!downloadDir.exists()) downloadDir.mkdirs()

            val targetFile = File(downloadDir, "${song.id}.mp3")

            // Download actual audio stream from the real URL
            val url = URL(song.audioUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.getInputStream().use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Verify the file is a real audio file (at least 10KB)
            if (!targetFile.exists() || targetFile.length() < 10_000) {
                // Not a valid audio file — clean up and report failure
                if (targetFile.exists()) targetFile.delete()
                return@withContext false
            }

            songDao.updateDownloadStatus(song.id, true, targetFile.absolutePath)
            true
        } catch (e: Exception) {
            // Network failure or any other error — do NOT create fake placeholder files
            false
        }
    }

    suspend fun removeDownload(song: Song) = withContext(Dispatchers.IO) {
        song.localFilePath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }
        songDao.updateDownloadStatus(song.id, false, null)
    }

    suspend fun createPlaylist(name: String, description: String, songIds: List<String>) = withContext(Dispatchers.IO) {
        val newPlaylist = PlaylistEntity(
            id = "pl_${UUID.randomUUID()}",
            title = name,
            description = description.ifBlank { "Curated by you" },
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            songIdsJson = songIds.joinToString(","),
            isAiGenerated = false
        )
        playlistDao.insertPlaylist(newPlaylist)
    }

    suspend fun deletePlaylist(playlistId: String) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun computeTasteProfile(
        vibe: DjVibe = DjVibe.BALANCED,
        selectedLanguages: Set<String> = setOf("Hindi", "Bengali")
    ): TasteProfile = withContext(Dispatchers.IO) {
        val totalCount = listeningDao.getTotalEventsCount()
        val skippedCount = listeningDao.getSkippedEventsCount()
        val avgEnergy = listeningDao.getAverageEnergy() ?: 0.75f
        val avgValence = listeningDao.getAverageValence() ?: 0.80f
        val topGenresTuple = listeningDao.getTopGenresCount()

        val topGenres = if (topGenresTuple.isNotEmpty()) {
            val totalGenreOccurrences = topGenresTuple.sumOf { it.count }.coerceAtLeast(1)
            topGenresTuple.take(4).map {
                com.example.data.model.GenreStat(
                    genre = it.genre,
                    percentage = (it.count * 100) / totalGenreOccurrences
                )
            }
        } else {
            if ("Hindi" in selectedLanguages || "Bengali" in selectedLanguages) {
                listOf(
                    com.example.data.model.GenreStat("Hindi Romantic", 45),
                    com.example.data.model.GenreStat("Bengali Romantic (বাংলা)", 30),
                    com.example.data.model.GenreStat("Rabindra Sangeet Fusion", 15),
                    com.example.data.model.GenreStat("Bollywood Dance", 10)
                )
            } else {
                listOf(
                    com.example.data.model.GenreStat("Synthwave", 40),
                    com.example.data.model.GenreStat("Indie Pop", 30),
                    com.example.data.model.GenreStat("Electronic", 20),
                    com.example.data.model.GenreStat("Lo-Fi", 10)
                )
            }
        }

        val dominant = topGenres.firstOrNull()?.genre ?: "Hindi Romantic"
        val skipRate = if (totalCount > 0) ((skippedCount * 100) / totalCount) else 4

        val now = System.currentTimeMillis()
        val timeOfDay = when (SimpleDateFormat("HH", Locale.getDefault()).format(Date(now)).toIntOrNull() ?: 12) {
            in 5..11 -> "morning"
            in 12..16 -> "afternoon"
            in 17..21 -> "evening"
            else -> "late night"
        }

        val recentEvents = listeningDao.getRecentEvents()
        val baseProfile = TasteProfile(
            averageEnergy = avgEnergy,
            averageValence = avgValence,
            dominantGenre = dominant,
            topGenres = topGenres,
            totalPlaysTracked = totalCount.coerceAtLeast(24),
            skipRatePercentage = skipRate,
            primaryMoodSummary = when {
                "Hindi" in selectedLanguages || "Bengali" in selectedLanguages ->
                    "Soulful Hindi romance & nostalgic Bengali melodies"
                avgEnergy > 0.8f -> "High adrenaline & electric momentum"
                avgEnergy < 0.45f -> "Introspective mellow chill"
                else -> "Harmonic melodic groove"
            },
            lastTrainedTimeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
        )

        val djResult = geminiDjService.generateDjCommentary(baseProfile, recentEvents, vibe, timeOfDay)

        baseProfile.copy(
            djIntroCommentary = djResult.commentary,
            primaryMoodSummary = djResult.moodSummary
        )
    }

    suspend fun getCuratedSongsForVibe(
        vibe: DjVibe,
        selectedLanguages: Set<String> = setOf("Hindi", "Bengali"),
        prioritizeLanguages: Boolean = true
    ): List<Song> = withContext(Dispatchers.IO) {
        val all = songDao.getAllSongs().map { it.toSong() }
        if (all.isEmpty()) return@withContext emptyList()

        // Sort songs based on proximity to vibe energy and valence + language priority bonus
        return@withContext all.sortedBy { song ->
            val energyDiff = kotlin.math.abs(song.energy - vibe.targetEnergy)
            val valenceDiff = kotlin.math.abs(song.valence - vibe.targetValence)
            val langPriorityPenalty = if (prioritizeLanguages && song.language !in selectedLanguages) 10.0f else 0.0f
            (energyDiff * 1.5f) + valenceDiff + langPriorityPenalty
        }
    }
}
