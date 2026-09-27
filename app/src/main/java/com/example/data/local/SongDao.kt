package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongsFlow(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE isFavorite = 1")
    fun getFavoriteSongsFlow(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isDownloaded = 1")
    fun getDownloadedSongsFlow(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: String): SongEntity?

    @Query("SELECT * FROM songs WHERE playCount > 0 ORDER BY lastPlayedTimestamp DESC LIMIT 20")
    fun getRecentlyPlayedFlow(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT 15")
    fun getHeavyRotationFlow(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongIfNotExists(song: SongEntity)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :songId")
    suspend fun updateFavorite(songId: String, isFavorite: Boolean)

    @Query("UPDATE songs SET isDownloaded = :isDownloaded, localFilePath = :localPath WHERE id = :songId")
    suspend fun updateDownloadStatus(songId: String, isDownloaded: Boolean, localPath: String?)

    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayedTimestamp = :timestamp WHERE id = :songId")
    suspend fun recordPlay(songId: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}
