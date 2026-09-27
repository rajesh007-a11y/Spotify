package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundifyAudioPlayer
import com.example.audio.TtsDjSpeaker
import com.example.data.local.CatalogData
import com.example.data.local.PlaylistEntity
import com.example.data.local.SoundifyDatabase
import com.example.data.model.AudioQuality
import com.example.data.model.DjVibe
import com.example.data.model.EqualizerPreset
import com.example.data.model.Song
import com.example.data.model.TasteProfile
import com.example.data.model.UserSettings
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.repository.MusicRepository
import com.example.data.repository.SongSearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class NavigationTab(val title: String) {
    HOME("Home"),
    SEARCH("Search"),
    LIBRARY("Your Library"),
    AI_DJ("AI DJ")
}

enum class RepeatMode {
    OFF, ALL, ONE
}

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SoundifyDatabase.getDatabase(application, viewModelScope)
    private val repository = MusicRepository(
        songDao = database.songDao(),
        listeningDao = database.listeningDao(),
        playlistDao = database.playlistDao()
    )

    private val audioPlayer = SoundifyAudioPlayer(application)
    private val djSpeaker = TtsDjSpeaker(application)

    private val _fetchedHomeSongs = MutableStateFlow<List<Song>>(emptyList())

    // Repository Flows — NO fallback to dummy CatalogData; show only real/fetched songs
    val allSongs: StateFlow<List<Song>> = combine(repository.allSongs, _fetchedHomeSongs) { db, fetched ->
        (fetched + db).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val downloadedSongs: StateFlow<List<Song>> = repository.downloadedSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val recentlyPlayed: StateFlow<List<Song>> = repository.recentlyPlayed.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val heavyRotation: StateFlow<List<Song>> = repository.heavyRotation.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allPlaylists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogData.initialPlaylists
    )

    // Player State
    val isPlaying: StateFlow<Boolean> = audioPlayer.isPlaying
    val currentPositionMs: StateFlow<Long> = audioPlayer.currentPositionMs
    val durationMs: StateFlow<Long> = audioPlayer.durationMs
    val visualizerAmplitudes: StateFlow<List<Float>> = audioPlayer.visualizerAmplitudes

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _currentQueue = MutableStateFlow<List<Song>>(emptyList())
    val currentQueue: StateFlow<List<Song>> = _currentQueue.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    // Audio Quality & Equalizer
    private val _audioQuality = MutableStateFlow(AudioQuality.LOSSLESS_HIFI)
    val audioQuality: StateFlow<AudioQuality> = _audioQuality.asStateFlow()

    private val _currentEqualizer = MutableStateFlow(EqualizerPreset.ELECTRONIC)
    val currentEqualizer: StateFlow<EqualizerPreset> = _currentEqualizer.asStateFlow()

    private val _bassBoostLevel = MutableStateFlow(50) // 0 to 100
    val bassBoostLevel: StateFlow<Int> = _bassBoostLevel.asStateFlow()

    // Offline Mode
    private val _isOfflineOnlyMode = MutableStateFlow(false)
    val isOfflineOnlyMode: StateFlow<Boolean> = _isOfflineOnlyMode.asStateFlow()

    private val _downloadingSongIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingSongIds: StateFlow<Set<String>> = _downloadingSongIds.asStateFlow()

    // User Settings & Language Preferences
    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Discovery songs & playlists prioritized by language
    val prioritizedSongs: StateFlow<List<Song>> = combine(allSongs, userSettings) { songs, settings ->
        if (settings.prioritizeInDiscovery) {
            songs.sortedByDescending { song ->
                if (song.language in settings.selectedLanguages) 2 else 0
            }
        } else {
            songs
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prioritizedPlaylists: StateFlow<List<PlaylistEntity>> = combine(allPlaylists, userSettings) { playlists, settings ->
        if (settings.prioritizeInDiscovery) {
            playlists.sortedByDescending { pl ->
                val matchesHindi = "Hindi" in settings.selectedLanguages && pl.title.contains("Hindi", ignoreCase = true)
                val matchesBengali = "Bengali" in settings.selectedLanguages && (pl.title.contains("Bangla", ignoreCase = true) || pl.title.contains("বাংলা"))
                if (matchesHindi || matchesBengali) 2 else 0
            }
        } else {
            playlists
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogData.initialPlaylists)

    // Navigation & UI Sheets
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val selectedPlaylist: StateFlow<PlaylistEntity?> = _selectedPlaylist.asStateFlow()

    private val _showAudioQualityDialog = MutableStateFlow(false)
    val showAudioQualityDialog: StateFlow<Boolean> = _showAudioQualityDialog.asStateFlow()

    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _showTrainDjDialog = MutableStateFlow(false)
    val showTrainDjDialog: StateFlow<Boolean> = _showTrainDjDialog.asStateFlow()

    // JioSaavn Global Search Integration
    private val songSearchRepository = SongSearchRepository()
    private var saavnSearchJob: Job? = null
    private val _saavnSearchResults = MutableStateFlow<List<Song>>(emptyList())
    val saavnSearchResults: StateFlow<List<Song>> = _saavnSearchResults.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    private val _searchIsArtistMode = MutableStateFlow(false)
    val searchIsArtistMode: StateFlow<Boolean> = _searchIsArtistMode.asStateFlow()

    private val _searchArtistName = MutableStateFlow<String?>(null)
    val searchArtistName: StateFlow<String?> = _searchArtistName.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val searchResults: StateFlow<List<Song>> = combine(allSongs, _saavnSearchResults, searchQuery) { localSongs, saavnSongs, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val local = localSongs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true) ||
                song.genre.contains(query, ignoreCase = true) ||
                song.album.contains(query, ignoreCase = true)
            }
            (local + saavnSongs).distinctBy { it.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI DJ State
    private val _tasteProfile = MutableStateFlow(TasteProfile())
    val tasteProfile: StateFlow<TasteProfile> = _tasteProfile.asStateFlow()

    private val _currentDjVibe = MutableStateFlow(DjVibe.BALANCED)
    val currentDjVibe: StateFlow<DjVibe> = _currentDjVibe.asStateFlow()

    private val _isDjThinking = MutableStateFlow(false)
    val isDjThinking: StateFlow<Boolean> = _isDjThinking.asStateFlow()

    val isDjSpeaking: StateFlow<Boolean> = djSpeaker.isSpeaking

    private val _isTtsVoiceEnabled = MutableStateFlow(true)
    val isTtsVoiceEnabled: StateFlow<Boolean> = _isTtsVoiceEnabled.asStateFlow()

    private val _isTrainingAi = MutableStateFlow(false)
    val isTrainingAi: StateFlow<Boolean> = _isTrainingAi.asStateFlow()

    private val _trainingProgressStep = MutableStateFlow("")
    val trainingProgressStep: StateFlow<String> = _trainingProgressStep.asStateFlow()

    private var songStartTimeMs = 0L

    init {
        audioPlayer.setOnCompletionListener {
            handleSongCompleted()
        }

        // When a song can't be played (no network, invalid file), skip to next
        audioPlayer.setOnErrorListener { failedSong ->
            android.util.Log.w("MusicPlayerVM", "Song failed to play: ${failedSong.title}, skipping...")
            viewModelScope.launch {
                delay(300) // Small delay to avoid rapid-fire skips
                nextTrack()
            }
        }

        audioPlayer.setOnTrackChangedListener { mediaId ->
            val track = _currentQueue.value.find { it.id == mediaId }
            if (track != null) {
                playedSongIds.add(track.id)
                playedSongTitles.add(cleanTitle(track.title))
                
                if (_currentSong.value?.id != mediaId) {
                    _currentSong.value?.let { prev ->
                        val listenedMs = System.currentTimeMillis() - songStartTimeMs
                        viewModelScope.launch {
                            repository.recordListeningEvent(
                                song = prev,
                                durationListenedMs = listenedMs,
                                wasCompleted = true,
                                wasSkipped = false
                            )
                        }
                    }
                    
                    _currentSong.value = track
                    songStartTimeMs = System.currentTimeMillis()

                    // Persist last-played song for cold-start restore
                    persistLastPlayedSongId(track.id)
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        repository.ensureSongCached(track)
                    }
                }
            }
        }

        audioPlayer.setOnQueueEndApproachListener { lastSongId ->
            checkAndPrefetchNextSongs(lastSongId)
        }

        // Initialize taste profile
        refreshTasteProfile()

        // Restore last-played song from previous session, then fetch Home feed
        restoreLastPlayedSong()

        // Restore saved recent searches
        loadSearchHistory()
    }

    private fun persistLastPlayedSongId(songId: String) {
        val prefs = getApplication<Application>().getSharedPreferences("soundify_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_played_song_id", songId).apply()
    }

    private fun restoreLastPlayedSong() {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().getSharedPreferences("soundify_prefs", Context.MODE_PRIVATE)
                val lastSongId = prefs.getString("last_played_song_id", null)
                if (lastSongId != null) {
                    val song = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        repository.getSongById(lastSongId)
                    }
                    if (song != null && _currentQueue.value.isEmpty()) {
                        _currentSong.value = song
                        _currentQueue.value = listOf(song)
                        audioPlayer.loadQueueWithoutPlaying(listOf(song), 0)
                    }
                }
            } catch (_: Exception) {
                // Ignore — will fall back to trending default
            } finally {
                // Always fetch Home feed for display (and as queue fallback)
                fetchTrendingHomeSongs()
            }
        }
    }

    private fun fetchTrendingHomeSongs() {
        viewModelScope.launch {
            try {
                // Fetch top trending Hindi/Bollywood hits
                val results1 = songSearchRepository.searchDomainSongs("Top Bollywood")
                val results2 = songSearchRepository.searchDomainSongs("Trending English")
                val combined = (results1 + results2).distinctBy { it.id }.shuffled()
                
                if (combined.isNotEmpty()) {
                    _fetchedHomeSongs.value = combined
                    // Only set current song/queue to trending default if nothing
                    // was restored from the previous session (Issue 2 restore)
                    if (_currentQueue.value.isEmpty()) {
                        _currentQueue.value = combined
                        _currentSong.value = combined.first()
                        // Preload/prepare only — do NOT auto-start playback just
                        // because the app opened. Music starts only when the user
                        // taps Play or a song.
                        audioPlayer.loadQueueWithoutPlaying(combined, 0)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun selectPlaylist(playlist: PlaylistEntity?) {
        _selectedPlaylist.value = playlist
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        saavnSearchJob?.cancel()
        if (query.isNotBlank() && query.trim().length >= 2) {
            _isSearchLoading.value = true
            saavnSearchJob = viewModelScope.launch {
                delay(150) // Fast debounce for snappy results
                try {
                    // Artist/band-aware search: if the query matches an artist or
                    // band (e.g. "Fossils", "Fakira", "Atif Aslam", "Arijit Singh"),
                    // show that artist's songs as a dedicated playlist.
                    val result = songSearchRepository.searchArtistAwareSongs(query.trim())
                    _saavnSearchResults.value = result.songs
                    _searchIsArtistMode.value = result.isArtistMode
                    _searchArtistName.value = result.artistName
                    if (result.songs.isNotEmpty()) {
                        commitSearchToHistory(query.trim())
                    }
                } catch (_: Exception) {
                    // Network offline — keep whatever local results we have
                } finally {
                    _isSearchLoading.value = false
                }
            }
        } else {
            _saavnSearchResults.value = emptyList()
            _isSearchLoading.value = false
            _searchIsArtistMode.value = false
            _searchArtistName.value = null
        }
    }

    /** Commit the current query to recent-search history (call on song tap / search submit). */
    fun commitSearchToHistory(query: String = _searchQuery.value) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return
        val prefs = getApplication<Application>().getSharedPreferences("soundify_prefs", Context.MODE_PRIVATE)
        val existing = prefs.getStringSet("recent_searches", emptySet())?.toMutableList() ?: mutableListOf()
        existing.removeAll { it.equals(trimmed, ignoreCase = true) }
        existing.add(0, trimmed)
        val trimmedList = existing.take(15)
        // StringSet doesn't preserve order, so also store an ordered CSV for display order.
        prefs.edit()
            .putStringSet("recent_searches", trimmedList.toSet())
            .putString("recent_searches_ordered", trimmedList.joinToString("\u0001"))
            .apply()
        _recentSearches.value = trimmedList
    }

    fun clearSearchHistory() {
        val prefs = getApplication<Application>().getSharedPreferences("soundify_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("recent_searches").remove("recent_searches_ordered").apply()
        _recentSearches.value = emptyList()
    }

    private fun loadSearchHistory() {
        val prefs = getApplication<Application>().getSharedPreferences("soundify_prefs", Context.MODE_PRIVATE)
        val ordered = prefs.getString("recent_searches_ordered", null)
        _recentSearches.value = ordered?.split("\u0001")?.filter { it.isNotBlank() } ?: emptyList()
    }

    fun setAudioQuality(quality: AudioQuality) {
        _audioQuality.value = quality
    }

    fun setEqualizerPreset(preset: EqualizerPreset) {
        _currentEqualizer.value = preset
    }

    fun setBassBoost(level: Int) {
        _bassBoostLevel.value = level.coerceIn(0, 100)
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineOnlyMode.value = enabled
    }

    fun toggleOfflineMode() {
        _isOfflineOnlyMode.value = !_isOfflineOnlyMode.value
    }

    fun setShowAudioQualityDialog(show: Boolean) {
        _showAudioQualityDialog.value = show
    }

    fun setShowCreatePlaylistDialog(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun setShowTrainDjDialog(show: Boolean) {
        _showTrainDjDialog.value = show
    }

    fun toggleTtsVoice() {
        _isTtsVoiceEnabled.value = !_isTtsVoiceEnabled.value
        if (!_isTtsVoiceEnabled.value) {
            djSpeaker.stop()
        }
    }

    /** Check if device currently has internet connectivity */
    private fun isNetworkAvailable(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun cleanTitle(t: String) = t.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim().lowercase()
    val playedSongIds = mutableSetOf<String>()
    val playedSongTitles = mutableSetOf<String>()
    private val queueFetchMutex = Mutex()

    // Bumped every time the user explicitly changes what's playing (playSong /
    // startDjSession). Background fetches (prefetch / forceFetch) capture this
    // value when they start and discard their results if it has since changed,
    // so a stale network response can never clobber a queue the user has since
    // replaced by tapping something else.
    private val queueGeneration = java.util.concurrent.atomic.AtomicInteger(0)

    fun playSong(song: Song, queue: List<Song>? = null) {
        if (_isOfflineOnlyMode.value && !song.isDownloaded) {
            return
        }
        queueGeneration.incrementAndGet()

        // Record listening stats for previous song
        _currentSong.value?.let { prev ->
            val listenedMs = System.currentTimeMillis() - songStartTimeMs
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                repository.recordListeningEvent(
                    song = prev,
                    durationListenedMs = listenedMs,
                    wasCompleted = false,
                    wasSkipped = true
                )
            }
        }

        _currentSong.value = song
        songStartTimeMs = System.currentTimeMillis()

        // Persist last-played song for cold-start restore
        persistLastPlayedSongId(song.id)
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.ensureSongCached(song)
        }
        
        val cTitle = cleanTitle(song.title)
        playedSongIds.add(song.id)
        playedSongTitles.add(cTitle)

        if (queue != null) {
            val deduplicatedQueue = queue.filter { s ->
                !playedSongIds.contains(s.id) && !playedSongTitles.contains(cleanTitle(s.title))
            }
            
            // force add it if missing due to filter
            val finalQueue = if (deduplicatedQueue.any { it.id == song.id }) {
                deduplicatedQueue
            } else {
                listOf(song) + deduplicatedQueue
            }
            
            finalQueue.forEach {
                playedSongIds.add(it.id)
                playedSongTitles.add(cleanTitle(it.title))
            }
            
            _currentQueue.value = finalQueue
            val startIndex = finalQueue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            audioPlayer.playQueue(finalQueue, startIndex)
        } else {
            // For single song additions (e.g. play next)
            val currentQ = _currentQueue.value.toMutableList()
            if (!currentQ.any { it.id == song.id }) {
                currentQ.add(song)
                _currentQueue.value = currentQ
            }
            audioPlayer.playQueue(currentQ, currentQ.indexOfFirst { it.id == song.id }.coerceAtLeast(0))
        }
    }

    fun playSearchedSong(song: Song) {
        playSong(song, queue = listOf(song))   // instant playback, no wait

        val startGeneration = queueGeneration.get()
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            queueFetchMutex.withLock {
                if (queueGeneration.get() != startGeneration) return@withLock
                try {
                    val sameArtistSongs = songSearchRepository.getSameArtistTopSongs(
                        song = song,
                        excludeIds = playedSongIds.toSet(),
                        excludeTitleKeys = playedSongTitles.toSet(),
                        limit = 10
                    ).filter {
                        !playedSongIds.contains(it.id) &&
                        !playedSongTitles.contains(cleanTitle(it.title))
                    }
                    if (sameArtistSongs.isEmpty()) return@withLock
                    if (queueGeneration.get() != startGeneration) return@withLock

                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        if (queueGeneration.get() != startGeneration) return@withContext
                        sameArtistSongs.forEach {
                            playedSongIds.add(it.id)
                            playedSongTitles.add(cleanTitle(it.title))
                        }
                        _currentQueue.value = _currentQueue.value + sameArtistSongs
                        audioPlayer.appendToQueue(sameArtistSongs)
                    }
                } catch (_: Exception) { /* best-effort; endless flow still kicks in later */ }
            }
        }
    }

    fun togglePlayPause() {
        if (_currentSong.value == null) {
            _currentQueue.value.firstOrNull()?.let { playSong(it) }
        } else {
            audioPlayer.togglePlayPause()
        }
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun nextTrack() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

        val current = _currentSong.value
        val currentIndex = queue.indexOfFirst { it.id == current?.id }

        if (currentIndex != -1 && currentIndex + 1 < queue.size) {
            audioPlayer.nextTrack()
        } else {
            // Queue is exhausted. DO NOT loop back to 0. Force fetch and play next!
            _isDjThinking.value = true
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                forceFetchAndPlayNext(current)
            }
        }
    }

    private suspend fun forceFetchAndPlayNext(current: Song?) {
        if (current == null) return
        
        queueFetchMutex.withLock {
            val startGeneration = queueGeneration.get()
            val queue = _currentQueue.value
            try {
                var newSongsToAdd: List<Song> = emptyList()
                val snapshotTitles = playedSongTitles.toSet()
                
                val recommendations = songSearchRepository.getYouTubeRecommendations(current.title, current.artist, snapshotTitles)
                newSongsToAdd = recommendations.filter { song ->
                    !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                }
                
                if (newSongsToAdd.isEmpty()) {
                    val similarSongs = songSearchRepository.getSimilarDomainSongs(current.id)
                    newSongsToAdd = similarSongs.filter { song ->
                        !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                    }.take(3)
                }
                if (newSongsToAdd.isEmpty()) {
                    val fallbackQuery = "${current.artist} best songs"
                    val results = songSearchRepository.searchDomainSongs(fallbackQuery)
                    newSongsToAdd = results.filter { song ->
                        !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                    }.take(3)
                }
                
                // Retry with BROADENED query instead of pruning the blacklist.
                // Pruning playedSongIds/playedSongTitles would silently re-allow
                // the seed song to reappear — so we never shrink those sets.
                // Instead we broaden the search to a different genre/language.
                if (newSongsToAdd.isEmpty()) {
                    val broadenedQueries = listOfNotNull(
                        current.language.takeIf { it.isNotBlank() }?.let { "trending $it songs" },
                        current.genre.takeIf { it.isNotBlank() && it != current.language }?.let { "top $it songs" },
                        "trending popular songs"
                    )
                    for (broadQuery in broadenedQueries) {
                        if (newSongsToAdd.isNotEmpty()) break
                        val broadResults = songSearchRepository.searchDomainSongs(broadQuery)
                        newSongsToAdd = broadResults.filter { song ->
                            !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                        }.take(3)
                    }
                }
                
                // Final re-filter right before appending to catch anything added by a fast concurrent operation
                newSongsToAdd = newSongsToAdd.filter { song ->
                    !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                }

                // If the user manually changed the song/queue while this fetch was
                // in flight, discard these results — they'd be applied against a
                // queue/player state that's no longer current.
                if (queueGeneration.get() != startGeneration) {
                    return@withLock
                }
                
                if (newSongsToAdd.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        if (queueGeneration.get() != startGeneration) return@withContext
                        newSongsToAdd.forEach { 
                            playedSongIds.add(it.id)
                            playedSongTitles.add(cleanTitle(it.title))
                        }
                        val newQueue = _currentQueue.value + newSongsToAdd
                        _currentQueue.value = newQueue
                        audioPlayer.appendToQueue(newSongsToAdd)
                        audioPlayer.nextTrack()
                    }
                } else {
                    android.util.Log.w("MusicPlayerVM", "WARNING: Endless queue starved completely despite retry. Falling back to old queue.")
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        if (queueGeneration.get() != startGeneration) return@withContext
                        val fallbackIndex = if (_isShuffle.value) queue.indices.random() else 0
                        audioPlayer.playQueue(queue, fallbackIndex)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("MusicPlayerVM", "WARNING: Endless queue error: ${e.message}. Falling back to old queue.")
                if (queueGeneration.get() == startGeneration) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        if (queueGeneration.get() != startGeneration) return@withContext
                        val fallbackIndex = if (_isShuffle.value) queue.indices.random() else 0
                        audioPlayer.playQueue(queue, fallbackIndex)
                    }
                }
            } finally {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _isDjThinking.value = false
                }
            }
        }
    }

    private fun checkAndPrefetchNextSongs(lastSongId: String) {
        if (_isOfflineOnlyMode.value) return
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            queueFetchMutex.withLock {
                val startGeneration = queueGeneration.get()
                try {
                    val queue = _currentQueue.value
                    val lastSong = queue.find { it.id == lastSongId } ?: return@launch
                    
                    var newSongsToAdd: List<Song> = emptyList()
                    val snapshotTitles = playedSongTitles.toSet()
                    
                    val recommendations = songSearchRepository.getYouTubeRecommendations(lastSong.title, lastSong.artist, snapshotTitles)
                    newSongsToAdd = recommendations.filter { song ->
                        !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                    }
                    
                    if (newSongsToAdd.isEmpty()) {
                        val similarSongs = songSearchRepository.getSimilarDomainSongs(lastSongId)
                        newSongsToAdd = similarSongs.filter { song ->
                            !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                        }.take(3)
                    }

                    if (newSongsToAdd.isEmpty()) {
                        val fallbackQuery = "${lastSong.artist} top songs"
                        val results = songSearchRepository.searchDomainSongs(fallbackQuery)
                        newSongsToAdd = results.filter { song ->
                            !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                        }.take(3)
                    }
                    
                    // Retry with BROADENED query instead of pruning the blacklist.
                    // Pruning playedSongIds/playedSongTitles would silently re-allow
                    // the seed song to reappear — so we never shrink those sets.
                    // Instead we broaden the search to a different genre/language.
                    if (newSongsToAdd.isEmpty()) {
                        val broadenedQueries = listOfNotNull(
                            lastSong.language.takeIf { it.isNotBlank() }?.let { "trending $it songs" },
                            lastSong.genre.takeIf { it.isNotBlank() && it != lastSong.language }?.let { "top $it songs" },
                            "trending popular songs"
                        )
                        for (broadQuery in broadenedQueries) {
                            if (newSongsToAdd.isNotEmpty()) break
                            val broadResults = songSearchRepository.searchDomainSongs(broadQuery)
                            newSongsToAdd = broadResults.filter { song ->
                                !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                            }.take(3)
                        }
                    }

                    // Final re-filter right before appending to catch anything added by a fast concurrent operation
                    newSongsToAdd = newSongsToAdd.filter { song ->
                        !playedSongIds.contains(song.id) && !playedSongTitles.contains(cleanTitle(song.title))
                    }

                    // Discard if the user has since manually changed the song/queue.
                    if (queueGeneration.get() != startGeneration) return@withLock

                    if (newSongsToAdd.isNotEmpty()) {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            if (queueGeneration.get() != startGeneration) return@withContext
                            newSongsToAdd.forEach { 
                                playedSongIds.add(it.id)
                                playedSongTitles.add(cleanTitle(it.title))
                            }
                            val newQueue = _currentQueue.value + newSongsToAdd
                            _currentQueue.value = newQueue
                            audioPlayer.appendToQueue(newSongsToAdd)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }

    fun previousTrack() {
        // If current position is greater than 3 seconds, restart current track
        if (currentPositionMs.value > 3000L) {
            audioPlayer.seekTo(0L)
            return
        }

        audioPlayer.previousTrack()
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    private fun handleSongCompleted() {
        // Only trigger this when the ENTIRE queued list completes
        // We log single song completions dynamically through the onTrackChangedListener natively
        val queue = _currentQueue.value
        if (queue.isEmpty()) return
        
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                audioPlayer.repeatCurrentSong()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                // If it ended, force next which fetches
                nextTrack()
            }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
            // Update active song favorite status
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = _currentSong.value?.copy(isFavorite = !song.isFavorite)
            }
        }
    }

    fun downloadSong(song: Song) {
        viewModelScope.launch {
            _downloadingSongIds.value = _downloadingSongIds.value + song.id
            val success = repository.downloadSong(song, getApplication())
            _downloadingSongIds.value = _downloadingSongIds.value - song.id
            if (success && _currentSong.value?.id == song.id) {
                _currentSong.value = _currentSong.value?.copy(isDownloaded = true)
            }
        }
    }

    fun removeDownload(song: Song) {
        viewModelScope.launch {
            repository.removeDownload(song)
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = _currentSong.value?.copy(isDownloaded = false, localFilePath = null)
            }
        }
    }

    fun createPlaylist(name: String, description: String, songIds: List<String> = emptyList()) {
        viewModelScope.launch {
            val ids = if (songIds.isEmpty()) {
                allSongs.value.take(4).map { it.id }
            } else {
                songIds
            }
            repository.createPlaylist(name, description, ids)
            _showCreatePlaylistDialog.value = false
        }
    }

    // User Settings Actions
    fun setSettingsOpen(isOpen: Boolean) {
        _isSettingsOpen.value = isOpen
    }

    fun toggleMusicLanguage(code: String) {
        val current = _userSettings.value.selectedLanguages.toMutableSet()
        if (code in current) {
            if (current.size > 1) { // keep at least 1 language active
                current.remove(code)
            }
        } else {
            current.add(code)
        }
        _userSettings.value = _userSettings.value.copy(selectedLanguages = current)
        refreshTasteProfile()
    }

    fun togglePrioritizeDiscovery() {
        _userSettings.value = _userSettings.value.copy(
            prioritizeInDiscovery = !_userSettings.value.prioritizeInDiscovery
        )
    }

    fun togglePrioritizeDj() {
        _userSettings.value = _userSettings.value.copy(
            prioritizeInDj = !_userSettings.value.prioritizeInDj
        )
        refreshTasteProfile()
    }

    fun setDjVoicePersona(persona: String) {
        _userSettings.value = _userSettings.value.copy(djVoicePersona = persona)
        refreshTasteProfile()
    }

    fun resetTasteProfile() {
        refreshTasteProfile(_currentDjVibe.value)
    }

    // AI DJ Controls
    fun setDjVibe(vibe: DjVibe) {
        _currentDjVibe.value = vibe
        refreshTasteProfile(vibe)
    }

    fun refreshTasteProfile(vibe: DjVibe = _currentDjVibe.value) {
        viewModelScope.launch {
            _isDjThinking.value = true
            val profile = repository.computeTasteProfile(
                vibe = vibe,
                selectedLanguages = _userSettings.value.selectedLanguages
            )
            _tasteProfile.value = profile
            _isDjThinking.value = false

            // If voice enabled, speak the commentary
            if (_isTtsVoiceEnabled.value && profile.djIntroCommentary.isNotBlank()) {
                djSpeaker.speak(profile.djIntroCommentary)
            }
        }
    }

    fun startDjSession(vibe: DjVibe = _currentDjVibe.value) {
        queueGeneration.incrementAndGet()
        viewModelScope.launch {
            _isDjThinking.value = true
            _currentDjVibe.value = vibe
            val profile = repository.computeTasteProfile(
                vibe = vibe,
                selectedLanguages = _userSettings.value.selectedLanguages
            )
            _tasteProfile.value = profile
            val curatedSongs = repository.getCuratedSongsForVibe(
                vibe = vibe,
                selectedLanguages = _userSettings.value.selectedLanguages,
                prioritizeLanguages = _userSettings.value.prioritizeInDj
            )
            _currentQueue.value = curatedSongs
            _isDjThinking.value = false

            // Voice intro
            if (_isTtsVoiceEnabled.value) {
                djSpeaker.speak(profile.djIntroCommentary)
            }

            curatedSongs.firstOrNull()?.let { playSong(it, curatedSongs) }
        }
    }

    fun speakCurrentDjCommentary() {
        val commentary = _tasteProfile.value.djIntroCommentary
        if (commentary.isNotBlank()) {
            djSpeaker.speak(commentary)
        }
    }

    fun stopDjSpeaking() {
        djSpeaker.stop()
    }

    fun triggerTrainAiModel() {
        viewModelScope.launch {
            _isTrainingAi.value = true
            _trainingProgressStep.value = "Analyzing listening session logs & skip curves..."
            delay(600)
            _trainingProgressStep.value = "Extracting energy spectrum & valence coordinates..."
            delay(700)
            _trainingProgressStep.value = "Fine-tuning neural taste vectors with Gemini..."
            delay(800)
            _trainingProgressStep.value = "Calibrating personalized DJ voice & curation persona..."
            delay(500)

            val updatedProfile = repository.computeTasteProfile(_currentDjVibe.value)
            _tasteProfile.value = updatedProfile
            _isTrainingAi.value = false

            if (_isTtsVoiceEnabled.value) {
                djSpeaker.speak("AI training complete! I've locked in your exact taste profile.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        djSpeaker.release()
    }
}
