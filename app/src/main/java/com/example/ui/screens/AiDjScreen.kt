package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DjVibe
import com.example.data.model.Song
import com.example.data.model.TasteProfile
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DjCyan
import com.example.ui.theme.DjPink
import com.example.ui.theme.DjPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AiDjScreen(
    tasteProfile: TasteProfile,
    currentVibe: DjVibe,
    curatedQueue: List<Song>,
    isDjThinking: Boolean,
    isDjSpeaking: Boolean,
    isTtsVoiceEnabled: Boolean,
    onSelectVibe: (DjVibe) -> Unit,
    onStartSession: (DjVibe) -> Unit,
    onOpenTrainAiDialog: () -> Unit,
    onToggleTtsVoice: () -> Unit,
    onSpeakCommentary: () -> Unit,
    onStopSpeaking: () -> Unit,
    onSongClick: (Song, List<Song>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for AI DJ Orb
    val infiniteTransition = rememberInfiniteTransition(label = "djOrbPulse")
    val pulseScaleOuter by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outerPulse"
    )
    val pulseScaleInner by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "innerPulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("ai_dj_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DJ SOUNDIFY",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DjCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isDjSpeaking) "SPEAKING" else "ONLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DjCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Your AI Personal DJ powered by Gemini",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Voice commentary switch button
                    IconButton(
                        onClick = onToggleTtsVoice,
                        modifier = Modifier.size(48.dp).testTag("toggle_dj_voice_button")
                    ) {
                        Icon(
                            imageVector = if (isTtsVoiceEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                            contentDescription = "Toggle DJ voice",
                            tint = if (isTtsVoiceEnabled) DjCyan else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Center Pulsing DJ Orb Hero
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing ambient ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScaleOuter)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    DjPurple.copy(alpha = 0.45f),
                                    DjCyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScaleInner)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(DjPurple, DjCyan, SpotifyGreen, DjPink, DjPurple)),
                            CircleShape
                        )
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF083344)))
                        )
                )

                // Inner core icon
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(DjPurple, DjCyan))
                        )
                        .clickable {
                            if (isDjSpeaking) onStopSpeaking() else onSpeakCommentary()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isDjThinking) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isDjSpeaking) Icons.Filled.RecordVoiceOver else Icons.Filled.AutoAwesome,
                            contentDescription = "DJ Voice",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }
        }

        // DJ Spoken Monologue Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(DjPurple, DjCyan)),
                        RoundedCornerShape(16.dp)
                    ),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.RecordVoiceOver,
                                contentDescription = null,
                                tint = DjCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DJ COMMENTARY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DjCyan,
                                letterSpacing = 1.sp
                            )
                        }

                        Row {
                            if (isDjSpeaking) {
                                IconButton(onClick = onStopSpeaking, modifier = Modifier.size(36.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Stop,
                                        contentDescription = "Stop",
                                        tint = DjPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            IconButton(onClick = onSpeakCommentary, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Speak commentary",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "\"${tasteProfile.djIntroCommentary}\"",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartSession(currentVibe) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("mix_with_dj_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpotifyGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play Mix",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mix & Play My Vibe",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Vibe Shifter Chips
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SHIFT THE VIBE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DjVibe.values()) { vibe ->
                    val isSelected = currentVibe == vibe
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectVibe(vibe) },
                        label = {
                            Text(
                                text = "${vibe.emoji} ${vibe.label}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DjCyan,
                            containerColor = DarkSurfaceElevated
                        )
                    )
                }
            }
        }

        // AI Model Taste Profile Card & Train Button
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Your AI Taste Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Trained: ${tasteProfile.lastTrainedTimeFormatted}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onOpenTrainAiDialog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DjPurple,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_train_dj_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = "Train AI",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Train Model", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Energy Meter
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Energy Target", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "${(tasteProfile.averageEnergy * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpotifyGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { tasteProfile.averageEnergy },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = SpotifyGreen,
                            trackColor = Color(0xFF333333)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Valence (Mood) Meter
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Mood Valence (Positivity)", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "${(tasteProfile.averageValence * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DjCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { tasteProfile.averageValence },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = DjCyan,
                            trackColor = Color(0xFF333333)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Top genres chips
                    Text(text = "Dominant Genre Taste", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tasteProfile.topGenres.take(3).forEach { stat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x33FFFFFF)
                            ) {
                                Text(
                                    text = "${stat.genre} (${stat.percentage}%)",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: AI Curated Queue
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CURATED FOR THIS SET (${curatedQueue.size} TRACKS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(curatedQueue) { song ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSongClick(song, curatedQueue) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.Transparent
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = song.albumArtUrl,
                        contentDescription = song.title,
                        modifier = Modifier
                            .size(48.dp)
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
                            text = "${song.artist} • ${song.genre}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x221DB954)
                    ) {
                        Text(
                            text = "${(song.energy * 100).toInt()}% BPM",
                            fontSize = 10.sp,
                            color = SpotifyGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
