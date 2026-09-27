package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.Song
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class BrowseCategory(
    val title: String,
    val color1: Color,
    val color2: Color,
    val queryTag: String
)

val BROWSE_CATEGORIES = listOf(
    BrowseCategory("Bollywood & Hindi", Color(0xFFF97316), Color(0xFFE11D48), "Hindi"),
    BrowseCategory("Bangla Hits (বাংলা)", Color(0xFF059669), Color(0xFF0284C7), "Bengali"),
    BrowseCategory("Arijit Singh Specials", Color(0xFF9333EA), Color(0xFFDB2777), "Arijit"),
    BrowseCategory("Rabindra Sangeet", Color(0xFFD97706), Color(0xFFEA580C), "Rabindra"),
    BrowseCategory("Synthwave", Color(0xFF7C3AED), Color(0xFFC026D3), "Synthwave"),
    BrowseCategory("Indie Pop", Color(0xFFE11D48), Color(0xFFFB7185), "Indie"),
    BrowseCategory("Lo-Fi / Chill", Color(0xFF059669), Color(0xFF10B981), "Lo-Fi"),
    BrowseCategory("Electronic", Color(0xFF2563EB), Color(0xFF38BDF8), "Electronic"),
    BrowseCategory("Workout Energy", Color(0xFFEA580C), Color(0xFFF97316), "Dance"),
    BrowseCategory("Hi-Fi Master", Color(0xFFD97706), Color(0xFFFBBF24), "Hi-Fi")
)

@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: List<Song>,
    allSongs: List<Song>,
    isSearchLoading: Boolean,
    onQueryChanged: (String) -> Unit,
    onSongClick: (Song, List<Song>) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onDownloadSong: (Song) -> Unit,
    isArtistMode: Boolean = false,
    artistName: String? = null,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    currentSong: Song? = null,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 16.dp)
            .testTag("search_screen")
    ) {
        // Title
        Text(
            text = "Search",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Text Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("What do you want to listen to?", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                if (isSearchLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SpotifyGreen,
                        strokeWidth = 2.dp
                    )
                } else if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear search",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .testTag("search_text_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotBlank()) {
            // Live Search Results
            if (searchResults.isEmpty() && !isSearchLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Check spelling or browse all categories",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (searchResults.isEmpty() && isSearchLoading) {
                // Show centered loading spinner when no results yet but still loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = SpotifyGreen,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Searching \"$searchQuery\"...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isArtistMode && !artistName.isNullOrBlank()) {
                        item {
                            Text(
                                text = "$artistName · Playlist",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(searchResults) { song ->
                        SearchResultRow(
                            song = song,
                            isCurrentSong = currentSong?.id == song.id,
                            isPlaying = isPlaying && currentSong?.id == song.id,
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onSongClick(song, if (isArtistMode) searchResults else listOf(song))
                            },
                            onToggleFavorite = { onToggleFavorite(song) },
                            onDownloadSong = { onDownloadSong(song) }
                        )
                    }
                }
            }
        } else {
            // Recent Searches
            if (recentSearches.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Recent searches",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Clear",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.clickable { onClearHistory() }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    recentSearches.take(8).forEach { pastQuery ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRecentSearchClick(pastQuery) }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = pastQuery, fontSize = 14.sp, color = TextPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Browse All Categories
            Text(
                text = "Browse all",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(BROWSE_CATEGORIES) { category ->
                    BrowseCategoryTile(
                        category = category,
                        onClick = { onQueryChanged(category.queryTag) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    song: Song,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDownloadSong: () -> Unit,
    isCurrentSong: Boolean = false,
    isPlaying: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = song.albumArtUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.DarkGray),
                    contentScale = ContentScale.Crop
                )
                if (isCurrentSong) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlaying) {
                            PlayingEqualizerBars()
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Pause,
                                contentDescription = "Paused",
                                tint = SpotifyGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrentSong) SpotifyGreen else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.artist} • ${song.genre}",
                    fontSize = 12.sp,
                    color = if (isCurrentSong) SpotifyGreen.copy(alpha = 0.8f) else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Download Status
            if (song.isDownloaded) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Downloaded",
                    tint = SpotifyGreen,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
            } else {
                IconButton(onClick = onDownloadSong) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Download",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Favorite Button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) SpotifyGreen else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayingEqualizerBars() {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val bars = remember { listOf(700, 500, 900) }
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.height(18.dp)) {
        bars.forEachIndexed { index, duration ->
            val heightFraction by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar$index"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.5.dp)
                    .width(3.dp)
                    .height((18.dp.value * heightFraction).dp)
                    .background(SpotifyGreen, RoundedCornerShape(1.dp))
            )
        }
    }
}
    category: BrowseCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(category.color1, category.color2)))
                .padding(12.dp)
        ) {
            Text(
                text = category.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
    }
}
