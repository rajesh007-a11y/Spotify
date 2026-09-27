package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArtUrl: String,
    val audioUrl: String,
    val genre: String,
    val energy: Float,
    val valence: Float,
    val bpm: Int,
    val lyrics: String,
    val language: String = "English",
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L
) {
    fun toSong(): Song = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        albumArtUrl = albumArtUrl,
        audioUrl = audioUrl,
        genre = genre,
        energy = energy,
        valence = valence,
        bpm = bpm,
        lyrics = lyrics,
        language = language,
        isFavorite = isFavorite,
        isDownloaded = isDownloaded,
        localFilePath = localFilePath,
        playCount = playCount,
        lastPlayedTimestamp = lastPlayedTimestamp
    )

    companion object {
        fun fromSong(song: Song): SongEntity = SongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            durationMs = song.durationMs,
            albumArtUrl = song.albumArtUrl,
            audioUrl = song.audioUrl,
            genre = song.genre,
            energy = song.energy,
            valence = song.valence,
            bpm = song.bpm,
            lyrics = song.lyrics,
            language = song.language,
            isFavorite = song.isFavorite,
            isDownloaded = song.isDownloaded,
            localFilePath = song.localFilePath,
            playCount = song.playCount,
            lastPlayedTimestamp = song.lastPlayedTimestamp
        )
    }
}
