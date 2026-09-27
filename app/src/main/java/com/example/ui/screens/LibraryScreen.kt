package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaylistEntity
import com.example.data.model.Song
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DjCyan
import com.example.ui.theme.DjPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    playlists: List<PlaylistEntity>,
    favoriteSongs: List<Song>,
    downloadedSongs: List<Song>,
    isOfflineOnlyMode: Boolean,
    onToggleOfflineMode: () -> Unit,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onLikedSongsClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onSongClick: (Song, List<Song>) -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("library_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenSettings() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(DjPurple, DjCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "R", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Your Library",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onCreatePlaylistClick,
                    modifier = Modifier.size(48.dp).testTag("create_playlist_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Playlist",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Filter Pills
        item {
            Spacer(modifier = Modifier.height(14.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "Downloaded", "Playlists", "Favorites")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    AssistChip(
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) SpotifyGreen else DarkSurfaceElevated
                        )
                    )
                }
            }
        }

        // Offline Mode Banner Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = DarkSurfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOfflineOnlyMode) Icons.Filled.OfflinePin else Icons.Filled.DownloadDone,
                            contentDescription = "Offline Mode",
                            tint = if (isOfflineOnlyMode) SpotifyGreen else TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isOfflineOnlyMode) "Offline Mode (Active)" else "Offline Listening Ready",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${downloadedSongs.size} tracks saved offline • Zero data used",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isOfflineOnlyMode,
                        onCheckedChange = { onToggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = SpotifyGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Liked Songs Playlist Tile (Shown if All, Playlists, or Favorites selected)
        if (selectedFilter != "Downloaded") {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLikedSongsClick() },
                    color = Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF450AF5), Color(0xFFC4EFD9)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Liked Songs",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Liked Songs",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Playlist • ${favoriteSongs.size} songs",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // If "Downloaded" is selected: show downloaded songs list
        if (selectedFilter == "Downloaded") {
            if (downloadedSongs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No downloaded tracks yet",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap the download icon on any song to listen without internet connection.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(downloadedSongs) { song ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(song, downloadedSongs) }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = song.albumArtUrl,
                                contentDescription = song.title,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${song.artist} • ${song.formattedDuration}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.DownloadDone,
                                contentDescription = "Offline ready",
                                tint = SpotifyGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Regular Playlists list
            items(playlists) { playlist ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPlaylistClick(playlist) },
                    color = Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = playlist.coverUrl,
                            contentDescription = playlist.title,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.DarkGray),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = playlist.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (playlist.isAiGenerated) "Curated by AI DJ Soundify" else "Playlist • Soundify User",
                                fontSize = 12.sp,
                                color = if (playlist.isAiGenerated) DjCyan else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
