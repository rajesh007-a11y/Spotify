package com.example.audio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.model.Song
import com.example.service.MusicPlaybackService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import kotlin.math.sin

class SoundifyAudioPlayer(private val context: Context) {

    companion object {
        var exoPlayer: ExoPlayer? = null
            private set

        fun getOrCreatePlayer(context: Context): ExoPlayer {
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(context.applicationContext).build()
            }
            return exoPlayer!!
        }
    }

    private val player = getOrCreatePlayer(context)

    private val playerScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _visualizerAmplitudes = MutableStateFlow(List(16) { 0.2f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    private var onSongCompletionListener: (() -> Unit)? = null
    private var onSongErrorListener: ((Song) -> Unit)? = null
    private var onQueueEndApproachListener: ((String) -> Unit)? = null
    private var onTrackChangedListener: ((String) -> Unit)? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                _isPlaying.value = false
                _currentPositionMs.value = 0L
                onSongCompletionListener?.invoke()
            }
            if (playbackState == Player.STATE_READY) {
                _durationMs.value = player.duration.coerceAtLeast(0L)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) startProgressTracker() else progressJob?.cancel()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            mediaItem?.mediaId?.let { onTrackChangedListener?.invoke(it) }
            
            if (player.mediaItemCount > 0 && player.currentMediaItemIndex >= player.mediaItemCount - 2) {
                val lastSongId = player.getMediaItemAt(player.mediaItemCount - 1).mediaId
                onQueueEndApproachListener?.invoke(lastSongId)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("SoundifyAudioPlayer", "ExoPlayer error", error)
            _isPlaying.value = false
        }
    }

    init {
        player.addListener(playerListener)
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onSongCompletionListener = listener
    }

    fun setOnErrorListener(listener: (Song) -> Unit) {
        onSongErrorListener = listener
    }

    fun setOnQueueEndApproachListener(listener: (String) -> Unit) {
        onQueueEndApproachListener = listener
    }

    fun setOnTrackChangedListener(listener: (String) -> Unit) {
        onTrackChangedListener = listener
    }

    fun createMediaItem(song: Song): MediaItem {
        val localFile = song.localFilePath?.let { File(it) }
        val hasValidLocalFile = localFile != null && localFile.exists() && localFile.length() > 10_000

        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setArtworkUri(Uri.parse(song.albumArtUrl))
            .build()

        val uri = if (hasValidLocalFile) Uri.fromFile(localFile) else Uri.parse(song.audioUrl)
        
        return MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(uri) // Ensure actual streaming URL is used here
            .setMediaMetadata(metadata)
            .build()
    }

    fun playQueue(queue: List<Song>, startIndex: Int = 0) {
        try {
            val mediaItems = queue.map { createMediaItem(it) }
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.prepare()
            player.play()

            val intent = Intent(context, MusicPlaybackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        } catch (e: Exception) {
            Log.e("SoundifyAudioPlayer", "Failed to start ExoPlayer for queue", e)
        }
    }

    /**
     * Loads a queue into the player and prepares it WITHOUT starting playback.
     * Use this for silent/background loads (e.g. app launch home feed) where the
     * user hasn't explicitly asked for music to start playing yet.
     */
    fun loadQueueWithoutPlaying(queue: List<Song>, startIndex: Int = 0) {
        try {
            val mediaItems = queue.map { createMediaItem(it) }
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
            player.prepare()
            // Intentionally no player.play() and no foreground service start here —
            // this just primes the player so the first tap on Play is instant.
        } catch (e: Exception) {
            Log.e("SoundifyAudioPlayer", "Failed to preload queue", e)
        }
    }

    fun appendToQueue(songs: List<Song>) {
        val mediaItems = songs.map { createMediaItem(it) }
        player.addMediaItems(mediaItems)
    }

    fun playSong(song: Song) {
        try {
            player.setMediaItem(createMediaItem(song))
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.prepare()
            player.play()

            val intent = Intent(context, MusicPlaybackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        } catch (e: Exception) {
            Log.e("SoundifyAudioPlayer", "Failed to start ExoPlayer for: ${song.title}", e)
            onSongErrorListener?.invoke(song)
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = playerScope.launch {
            while (isActive && player.isPlaying) {
                _currentPositionMs.value = player.currentPosition
                
                val pos = _currentPositionMs.value
                val newAmps = List(16) { index ->
                    val wave = (sin((pos / 150.0) + index * 0.6) * 0.5 + 0.5).toFloat()
                    0.15f + (wave * 0.75f)
                }
                _visualizerAmplitudes.value = newAmps
                
                delay(200)
            }
        }
    }

    fun resume() {
        player.play()
    }

    fun pause() {
        player.pause()
        _visualizerAmplitudes.value = List(16) { 0.1f }
    }

    fun togglePlayPause() {
        if (player.isPlaying) pause() else resume()
    }

    fun nextTrack() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    fun previousTrack() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        }
    }

    fun repeatCurrentSong() {
        player.seekTo(0L)
        player.play()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()
        _isPlaying.value = false
        progressJob?.cancel()
    }

    fun release() {
        player.removeListener(playerListener)
        stop()
    }
}
