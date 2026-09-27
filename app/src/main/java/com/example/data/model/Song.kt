package com.example.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArtUrl: String,
    val audioUrl: String,
    val genre: String,
    val energy: Float, // 0.0 to 1.0 (chill to hype)
    val valence: Float, // 0.0 to 1.0 (melancholic to euphoric)
    val bpm: Int,
    val lyrics: String,
    val language: String = "English",
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L
) {
    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
