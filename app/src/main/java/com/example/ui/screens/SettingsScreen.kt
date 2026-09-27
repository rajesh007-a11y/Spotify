package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AVAILABLE_MUSIC_LANGUAGES
import com.example.data.model.AudioQuality
import com.example.data.model.MusicLanguage
import com.example.data.model.UserSettings
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DjCyan
import com.example.ui.theme.DjPurple
import com.example.ui.theme.HiFiGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    audioQuality: AudioQuality,
    isOfflineMode: Boolean,
    onBack: () -> Unit,
    onToggleLanguage: (String) -> Unit,
    onTogglePrioritizeDiscovery: () -> Unit,
    onTogglePrioritizeDj: () -> Unit,
    onSelectVoicePersona: (String) -> Unit,
    onToggleOfflineMode: () -> Unit,
    onOpenAudioQuality: () -> Unit,
    onResetTasteProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Settings & Preferences",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Profile Header Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = DarkSurfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(DjPurple, DjCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userSettings.userName.take(1),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userSettings.userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = SpotifyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ad-Free Hi-Fi Plan • 100% Free",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SpotifyGreen
                            )
                        }
                    }
                }
            }
        }

        // SECTION: MUSIC LANGUAGES (Core Feature)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MUSIC LANGUAGES & REGIONAL PREFERENCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyGreen,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Select your preferred languages. The AI DJ and Home discovery feeds will prioritize tracks in these languages (Hindi and Bengali are prioritized).",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Language Selectable Cards
        items(AVAILABLE_MUSIC_LANGUAGES) { lang ->
            val isSelected = lang.code in userSettings.selectedLanguages

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) SpotifyGreen else Color(0xFF333333),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleLanguage(lang.code) }
                    .testTag("lang_toggle_${lang.code.lowercase()}"),
                color = if (isSelected) DarkSurfaceElevated else DarkSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lang.emoji,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lang.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${lang.nativeScript})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) SpotifyGreen else TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = lang.popularArtists,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SpotifyGreen else Color(0xFF333333)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECTION: AI DJ PERSONALIZATION
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = DjCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI DJ & DISCOVERY SETTINGS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DjCyan,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prioritize in Discovery Switch
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = DarkSurfaceElevated
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Prioritize in Discovery Feed",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Boost Hindi & Bengali songs in Home carousels and playlists",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = userSettings.prioritizeInDiscovery,
                                onCheckedChange = { onTogglePrioritizeDiscovery() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = SpotifyGreen,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = Color(0xFF333333)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Prioritize in AI DJ Curation",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "DJ Soundify queues and introduces Hindi & Bengali favorites",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = userSettings.prioritizeInDj,
                                onCheckedChange = { onTogglePrioritizeDj() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = DjCyan,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = Color(0xFF333333)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DJ Voice Persona Options
                Text(
                    text = "DJ Voice Persona",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val personas = listOf(
                    "Desi Radio RJ (Hindi & Bengali Mix)",
                    "Urban Kolkata Cafe Host (Bengali Focus)",
                    "Energetic Bollywood RJ",
                    "Global Radio Host (English)"
                )

                personas.forEach { persona ->
                    val isChosen = userSettings.djVoicePersona == persona
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectVoicePersona(persona) },
                        color = if (isChosen) DarkSurfaceElevated else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isChosen) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isChosen) DjCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = persona,
                                fontSize = 13.sp,
                                color = if (isChosen) TextPrimary else TextSecondary,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // SECTION: AUDIO & PLAYBACK SETTINGS
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "AUDIO QUALITY & PLAYBACK",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenAudioQuality() },
                    color = DarkSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = HiFiGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Streaming Quality",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${audioQuality.title} (${audioQuality.bitrateLabel})",
                                    fontSize = 12.sp,
                                    color = if (audioQuality == AudioQuality.LOSSLESS_HIFI) HiFiGold else SpotifyGreen
                                )
                            }
                        }

                        Text(
                            text = "Change",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpotifyGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Offline Mode
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = DarkSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.OfflinePin,
                                contentDescription = null,
                                tint = if (isOfflineMode) SpotifyGreen else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Offline Mode Only",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Play downloaded tracks without network connection",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = { onToggleOfflineMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = SpotifyGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Color(0xFF333333)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset Model Button
                Button(
                    onClick = onResetTasteProfile,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = null,
                        tint = DjPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Re-calibrate AI DJ Taste Profile")
                }
            }
        }

        // Footer info
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Soundify • Free Ad-Free Music Player",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = "Version 2.4 • Powered by Gemini 3.5 Flash",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
