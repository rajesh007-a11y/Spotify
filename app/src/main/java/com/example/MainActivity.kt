package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AudioQualityDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NowPlayingSheet
import com.example.ui.components.SpotifyBottomNav
import com.example.ui.components.TrainDjDialog
import com.example.ui.screens.AiDjScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MusicPlayerViewModel
import com.example.viewmodel.NavigationTab
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var controllerFuture: com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    SoundifyApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = androidx.media3.session.SessionToken(
            this,
            android.content.ComponentName(this, com.example.service.MusicPlaybackService::class.java)
        )
        controllerFuture = androidx.media3.session.MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.addListener({
            // Controller connection established. Media3 automatically syncs UI with Service.
        }, androidx.core.content.ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let {
            androidx.media3.session.MediaController.releaseFuture(it)
        }
    }
}

@Composable
fun SoundifyApp(viewModel: MusicPlayerViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val audioQuality by viewModel.audioQuality.collectAsState()
    
    val downloadingSongIds by viewModel.downloadingSongIds.collectAsState()
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.collectAsState()
    val tasteProfile by viewModel.tasteProfile.collectAsState()
    
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()
    
    // Dialogs
    val showAudioQualityDialog by viewModel.showAudioQualityDialog.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()
    val showTrainDjDialog by viewModel.showTrainDjDialog.collectAsState()

    // --- Back navigation: remembers which tabs the user actually visited, so
    // pressing back steps through tab history like Spotify, instead of
    // instantly closing the app. Only exits (with a confirm double-tap) once
    // there's nowhere left to go back to (i.e. back on Home).
    val tabBackStack = remember { mutableStateListOf(currentTab) }
    val context = LocalContext.current
    var exitArmed by remember { mutableStateOf(false) }

    LaunchedEffect(exitArmed) {
        if (exitArmed) {
            delay(2000)
            exitArmed = false
        }
    }

    fun navigateToTab(tab: NavigationTab) {
        if (tabBackStack.lastOrNull() != tab) {
            tabBackStack.add(tab)
        }
        viewModel.setTab(tab)
        viewModel.selectPlaylist(null)
        viewModel.setSettingsOpen(false)
    }

    BackHandler(enabled = true) {
        when {
            isNowPlayingExpanded -> viewModel.setNowPlayingExpanded(false)
            isSettingsOpen -> viewModel.setSettingsOpen(false)
            selectedPlaylist != null -> viewModel.selectPlaylist(null)
            tabBackStack.size > 1 -> {
                tabBackStack.removeAt(tabBackStack.lastIndex)
                viewModel.setTab(tabBackStack.last())
            }
            else -> {
                // Already on Home with no history left — require a second
                // back-press to actually exit, like Spotify/most apps.
                if (exitArmed) {
                    (context as? android.app.Activity)?.finish()
                } else {
                    exitArmed = true
                    android.widget.Toast.makeText(context, "Press back again to exit", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                if (currentSong != null && !isNowPlayingExpanded) {
                    MiniPlayerBar(
                        song = currentSong,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPositionMs,
                        durationMs = durationMs,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onExpand = { viewModel.setNowPlayingExpanded(true) }
                    )
                }
                SpotifyBottomNav(
                    selectedTab = currentTab,
                    onTabSelected = { navigateToTab(it) }
                )
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isSettingsOpen) {
                val userSettings by viewModel.userSettings.collectAsState()
                SettingsScreen(
                    userSettings = userSettings,
                    audioQuality = audioQuality,
                    isOfflineMode = viewModel.isOfflineOnlyMode.collectAsState().value,
                    onBack = { viewModel.setSettingsOpen(false) },
                    onToggleLanguage = { viewModel.toggleMusicLanguage(it) },
                    onTogglePrioritizeDiscovery = { viewModel.togglePrioritizeDiscovery() },
                    onTogglePrioritizeDj = { viewModel.togglePrioritizeDj() },
                    onSelectVoicePersona = { viewModel.setDjVoicePersona(it) },
                    onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                    onOpenAudioQuality = { viewModel.setShowAudioQualityDialog(true) },
                    onResetTasteProfile = { viewModel.resetTasteProfile() }
                )
            } else if (selectedPlaylist != null) {
                PlaylistDetailScreen(
                    playlist = selectedPlaylist!!,
                    songsInPlaylist = viewModel.allSongs.collectAsState().value,
                    onBack = { viewModel.selectPlaylist(null) },
                    onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                    onPlayAll = { },
                    onShuffleAll = { },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onDownloadSong = { viewModel.downloadSong(it) }
                )
            } else {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            songs = viewModel.prioritizedSongs.collectAsState().value,
                            recentlyPlayed = viewModel.recentlyPlayed.collectAsState().value,
                            heavyRotation = viewModel.heavyRotation.collectAsState().value,
                            playlists = viewModel.prioritizedPlaylists.collectAsState().value,
                            tasteProfile = tasteProfile,
                            selectedLanguages = viewModel.userSettings.collectAsState().value.selectedLanguages,
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onPlaylistClick = { viewModel.selectPlaylist(it) },
                            onTuneIntoDj = { viewModel.startDjSession(it) },
                            onOpenDjStudio = { navigateToTab(NavigationTab.AI_DJ) },
                            onOpenAudioQuality = { viewModel.setShowAudioQualityDialog(true) },
                            onOpenSettings = { viewModel.setSettingsOpen(true) }
                        )
                    }
                    NavigationTab.SEARCH -> {
                        SearchScreen(
                            searchQuery = viewModel.searchQuery.collectAsState().value,
                            searchResults = viewModel.searchResults.collectAsState().value,
                            allSongs = viewModel.allSongs.collectAsState().value,
                            isSearchLoading = viewModel.isSearchLoading.collectAsState().value,
                            onQueryChanged = { viewModel.setSearchQuery(it) },
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onDownloadSong = { viewModel.downloadSong(it) },
                            isArtistMode = viewModel.searchIsArtistMode.collectAsState().value,
                            artistName = viewModel.searchArtistName.collectAsState().value,
                            recentSearches = viewModel.recentSearches.collectAsState().value,
                            onRecentSearchClick = { viewModel.setSearchQuery(it) },
                            onClearHistory = { viewModel.clearSearchHistory() },
                            currentSong = currentSong,
                            isPlaying = isPlaying
                        )
                    }
                    NavigationTab.LIBRARY -> {
                        LibraryScreen(
                            playlists = viewModel.allPlaylists.collectAsState().value,
                            favoriteSongs = viewModel.favoriteSongs.collectAsState().value,
                            downloadedSongs = viewModel.downloadedSongs.collectAsState().value,
                            isOfflineOnlyMode = viewModel.isOfflineOnlyMode.collectAsState().value,
                            onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                            onPlaylistClick = { viewModel.selectPlaylist(it) },
                            onLikedSongsClick = { }, // Handle via inner navigation or expand if implemented
                            onCreatePlaylistClick = { viewModel.setShowCreatePlaylistDialog(true) },
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) },
                            onOpenSettings = { viewModel.setSettingsOpen(true) }
                        )
                    }
                    NavigationTab.AI_DJ -> {
                        AiDjScreen(
                            tasteProfile = tasteProfile,
                            currentVibe = viewModel.currentDjVibe.collectAsState().value,
                            curatedQueue = viewModel.currentQueue.collectAsState().value,
                            isDjThinking = viewModel.isDjThinking.collectAsState().value,
                            isDjSpeaking = viewModel.isDjSpeaking.collectAsState().value,
                            isTtsVoiceEnabled = viewModel.isTtsVoiceEnabled.collectAsState().value,
                            onSelectVibe = { viewModel.setDjVibe(it) },
                            onStartSession = { viewModel.startDjSession(it) },
                            onOpenTrainAiDialog = { viewModel.setShowTrainDjDialog(true) },
                            onToggleTtsVoice = { viewModel.toggleTtsVoice() },
                            onSpeakCommentary = { viewModel.speakCurrentDjCommentary() },
                            onStopSpeaking = { viewModel.stopDjSpeaking() },
                            onSongClick = { song, queue -> viewModel.playSong(song, queue) }
                        )
                    }
                }
            }
        }
    }
    
    // Now Playing Full Screen Sheet Overlay
    AnimatedVisibility(
        visible = isNowPlayingExpanded,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.fillMaxSize()
    ) {
        NowPlayingSheet(
            song = currentSong,
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            isShuffle = isShuffle,
            repeatMode = repeatMode,
            audioQuality = audioQuality,
            isDownloading = currentSong?.id in downloadingSongIds,
            visualizerAmplitudes = visualizerAmplitudes,
            djCommentary = tasteProfile.djIntroCommentary,
            onCollapse = { viewModel.setNowPlayingExpanded(false) },
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onSeekTo = { viewModel.seekTo(it) },
            onNext = { viewModel.nextTrack() },
            onPrevious = { viewModel.previousTrack() },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onToggleRepeat = { viewModel.toggleRepeat() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onDownloadSong = { viewModel.downloadSong(it) },
            onOpenQualityDialog = { viewModel.setShowAudioQualityDialog(true) },
            onSpeakDjCommentary = { viewModel.speakCurrentDjCommentary() }
        )
    }

    // Dialogs
    if (showAudioQualityDialog) {
        AudioQualityDialog(
            selectedQuality = audioQuality,
            currentPreset = viewModel.currentEqualizer.collectAsState().value,
            bassBoostLevel = viewModel.bassBoostLevel.collectAsState().value,
            onSelectQuality = { viewModel.setAudioQuality(it) },
            onSelectPreset = { viewModel.setEqualizerPreset(it) },
            onBassBoostChanged = { viewModel.setBassBoost(it) },
            onDismiss = { viewModel.setShowAudioQualityDialog(false) }
        )
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onCreate = { name, desc -> viewModel.createPlaylist(name, desc) },
            onDismiss = { viewModel.setShowCreatePlaylistDialog(false) }
        )
    }

    if (showTrainDjDialog) {
        TrainDjDialog(
            tasteProfile = tasteProfile,
            isTraining = viewModel.isTrainingAi.collectAsState().value,
            progressStep = viewModel.trainingProgressStep.collectAsState().value,
            onTrainClick = { viewModel.triggerTrainAiModel() },
            onDismiss = { viewModel.setShowTrainDjDialog(false) }
        )
    }
}
