package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SongEntity::class,
        ListeningEventEntity::class,
        PlaylistEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SoundifyDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun listeningDao(): ListeningDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: SoundifyDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): SoundifyDatabase {
            return INSTANCE ?: synchronized(this) {
                var createdInstance: SoundifyDatabase? = null
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundifyDatabase::class.java,
                    "soundify_music.db"
                ).fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        createdInstance?.let { seedData(it, coroutineScope) }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        createdInstance?.let { seedData(it, coroutineScope) }
                    }
                }).build()
                createdInstance = instance
                INSTANCE = instance
                seedData(instance, coroutineScope)
                instance
            }
        }

        private fun seedData(database: SoundifyDatabase, coroutineScope: CoroutineScope) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    // database.songDao().insertSongs(songEntities)
                    database.playlistDao().insertPlaylists(CatalogData.initialPlaylists)

                    if (database.listeningDao().getTotalEventsCount() < 3) {
                        val initialEvents = listOf(
                            ListeningEventEntity(
                                songId = "song_hi_1",
                                songTitle = "Kesariya",
                                artistName = "Arijit Singh & Pritam",
                                genre = "Hindi Romantic",
                                energy = 0.82f,
                                valence = 0.88f,
                                listenedDurationMs = 268000L,
                                wasCompleted = true,
                                wasSkipped = false,
                                wasLiked = true,
                                timeOfDay = "Evening"
                            ),
                            ListeningEventEntity(
                                songId = "song_bn_2",
                                songTitle = "Bhalobashar Morshum",
                                artistName = "Arijit Singh & Shreya Ghoshal",
                                genre = "Bengali Romantic",
                                energy = 0.72f,
                                valence = 0.86f,
                                listenedDurationMs = 238000L,
                                wasCompleted = true,
                                wasSkipped = false,
                                wasLiked = true,
                                timeOfDay = "Night"
                            ),
                            ListeningEventEntity(
                                songId = "song_hi_2",
                                songTitle = "Tum Hi Ho",
                                artistName = "Arijit Singh",
                                genre = "Hindi Romantic",
                                energy = 0.65f,
                                valence = 0.60f,
                                listenedDurationMs = 262000L,
                                wasCompleted = true,
                                wasSkipped = false,
                                wasLiked = true,
                                timeOfDay = "Night"
                            ),
                            ListeningEventEntity(
                                songId = "song_bn_1",
                                songTitle = "Ami Banglay Gaan Gai",
                                artistName = "Pratul Mukhopadhyay",
                                genre = "Bengali Classic",
                                energy = 0.65f,
                                valence = 0.90f,
                                listenedDurationMs = 285000L,
                                wasCompleted = true,
                                wasSkipped = false,
                                wasLiked = true,
                                timeOfDay = "Morning"
                            )
                        )
                        initialEvents.forEach { database.listeningDao().recordEvent(it) }
                    }
                } catch (e: Exception) {
                    // Ignore transient background seed exceptions
                }
            }
        }
    }
}
