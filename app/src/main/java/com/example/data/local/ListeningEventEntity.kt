package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "listening_events")
data class ListeningEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val songId: String,
    val songTitle: String,
    val artistName: String,
    val genre: String,
    val energy: Float,
    val valence: Float,
    val listenedDurationMs: Long,
    val wasCompleted: Boolean,
    val wasSkipped: Boolean,
    val wasLiked: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val timeOfDay: String
)

@Dao
interface ListeningDao {
    @Query("SELECT * FROM listening_events ORDER BY timestamp DESC LIMIT 100")
    fun getAllEventsFlow(): Flow<List<ListeningEventEntity>>

    @Query("SELECT * FROM listening_events ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentEvents(): List<ListeningEventEntity>

    @Insert
    suspend fun recordEvent(event: ListeningEventEntity)

    @Query("SELECT genre, COUNT(*) as count FROM listening_events GROUP BY genre ORDER BY count DESC")
    suspend fun getTopGenresCount(): List<GenreCountTuple>

    @Query("SELECT AVG(energy) FROM listening_events")
    suspend fun getAverageEnergy(): Float?

    @Query("SELECT AVG(valence) FROM listening_events")
    suspend fun getAverageValence(): Float?

    @Query("SELECT COUNT(*) FROM listening_events")
    suspend fun getTotalEventsCount(): Int

    @Query("SELECT COUNT(*) FROM listening_events WHERE wasSkipped = 1")
    suspend fun getSkippedEventsCount(): Int
}

data class GenreCountTuple(
    val genre: String,
    val count: Int
)
