package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.data.model.DjVibe
import com.example.data.model.Song
import com.example.data.model.TasteProfile
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DjCyan
import com.example.ui.theme.DjPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    songs: List<Song>,
    recentlyPlayed: List<Song>,
    heavyRotation: List<Song>,
    playlists: List<PlaylistEntity>,
    tasteProfile: TasteProfile,
    selectedLanguages: Set<String>,
    onSongClick: (Song, List<Song>) -> Unit,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onTuneIntoDj: (DjVibe) -> Unit,
    onOpenDjStudio: () -> Unit,
    onOpenAudioQuality: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = rememberGreeting()

    // DJ orb animation
    val infiniteTransition = rememberInfiniteTransition(label = "djPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = greeting,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Free & Ad-free badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SpotifyGreen.copy(alpha = 0.18f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Ad-free",
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ad-Free Hi-Fi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpotifyGreen
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenAudioQuality,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = "Audio Equalizer",
                                tint = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.size(44.dp).testTag("home_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Pills
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = onOpenSettings,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = null,
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val langLabel = if (selectedLanguages.isNotEmpty()) {
                                    selectedLanguages.joinToString(", ")
                                } else {
                                    "Languages"
                                }
                                Text(langLabel, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = SpotifyGreen.copy(alpha = 0.22f))
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text("All", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = DarkSurfaceElevated)
                    )
                    AssistChip(
                        onClick = onOpenDjStudio,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = DjCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI DJ Mixes", color = DjCyan, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = DarkSurfaceElevated)
                    )
                }
            }
        }

        // Quick Access 6-item Grid (Spotify Style 2-column tiles)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                val gridSongs = songs.take(6)
                for (row in 0 until (gridSongs.size + 1) / 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val item1 = gridSongs.getOrNull(row * 2)
                        val item2 = gridSongs.getOrNull(row * 2 + 1)

                        if (item1 != null) {
                            QuickAccessTile(
                                song = item1,
                                onClick = { onSongClick(item1, songs) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        if (item2 != null) {
                            QuickAccessTile(
                                song = item2,
                                onClick = { onSongClick(item2, songs) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // SPOTIFY DJ HERO BANNER
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(DjPurple, DjCyan, SpotifyGreen)),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onOpenDjStudio() }
                    .testTag("home_ai_dj_card"),
                color = DarkSurface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E1035),
                                    Color(0xFF0F2625),
                                    DarkSurface
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Pulsing animated DJ Orb
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(DjPurple, DjCyan))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "DJ",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "DJ • SOUNDIFY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DjCyan,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Your Personal AI DJ",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            // Big circular play button
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SpotifyGreen)
                                    .clickable { onTuneIntoDj(DjVibe.BALANCED) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play DJ",
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Spoken DJ commentary preview
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x33000000),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = DjCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tasteProfile.djIntroCommentary,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Recently Played
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Recently played")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val list = if (recentlyPlayed.isNotEmpty()) recentlyPlayed else songs.take(5)
                items(list) { song ->
                    SongMediaCard(
                        song = song,
                        onClick = { onSongClick(song, list) }
                    )
                }
            }
        }

        // Section: Made For You (AI Curated Mixes)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Made For You")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistMediaCard(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) }
                    )
                }
            }
        }

        // Section: Heavy Rotation
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Heavy Rotation")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val list = if (heavyRotation.isNotEmpty()) heavyRotation else songs.shuffled()
                items(list) { song ->
                    SongMediaCard(
                        song = song,
                        onClick = { onSongClick(song, list) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessTile(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        color = DarkSurfaceElevated
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = song.albumArtUrl,
                contentDescription = song.title,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = song.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
private fun SongMediaCard(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(138.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = song.albumArtUrl,
            contentDescription = song.title,
            modifier = Modifier
                .size(138.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${song.artist} • ${song.genre}",
            fontSize = 11.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlaylistMediaCard(
    playlist: PlaylistEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(148.dp)
            .clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = playlist.coverUrl,
                contentDescription = playlist.title,
                modifier = Modifier
                    .size(148.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )

            if (playlist.isAiGenerated) {
                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = DjPurple.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "AI DJ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = playlist.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = playlist.description,
            fontSize = 11.sp,
            color = TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
