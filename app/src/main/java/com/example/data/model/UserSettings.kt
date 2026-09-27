package com.example.data.model

data class UserSettings(
    val selectedLanguages: Set<String> = setOf("Hindi", "Bengali"),
    val prioritizeInDiscovery: Boolean = true,
    val prioritizeInDj: Boolean = true,
    val djVoicePersona: String = "Desi Radio RJ (Hindi & Bengali Mix)",
    val userName: String = "Rajesh",
    val djSpeechSpeed: Float = 1.05f,
    val djSpeechPitch: Float = 0.95f,
    val isAdFreeLifetime: Boolean = true
)

data class MusicLanguage(
    val code: String,
    val displayName: String,
    val nativeScript: String,
    val popularArtists: String,
    val emoji: String
)

val AVAILABLE_MUSIC_LANGUAGES = listOf(
    MusicLanguage(
        code = "Hindi",
        displayName = "Hindi",
        nativeScript = "हिंदी",
        popularArtists = "Arijit Singh, Pritam, Shreya Ghoshal, Jubin Nautiyal",
        emoji = "🇮🇳"
    ),
    MusicLanguage(
        code = "Bengali",
        displayName = "Bengali",
        nativeScript = "বাংলা",
        popularArtists = "Nachiketa, Somlata, Arijit Singh, Pratul Mukhopadhyay",
        emoji = "🪕"
    ),
    MusicLanguage(
        code = "English",
        displayName = "English & Global",
        nativeScript = "English",
        popularArtists = "Neon Horizon, The Weeknd, Dua Lipa, Synthwave",
        emoji = "🌐"
    ),
    MusicLanguage(
        code = "Punjabi",
        displayName = "Punjabi",
        nativeScript = "ਪੰਜਾਬੀ",
        popularArtists = "Diljit Dosanjh, AP Dhillon, Karan Aujla",
        emoji = "🥁"
    ),
    MusicLanguage(
        code = "Tamil",
        displayName = "Tamil",
        nativeScript = "தமிழ்",
        popularArtists = "Anirudh Ravichander, A.R. Rahman, Sid Sriram",
        emoji = "🪘"
    ),
    MusicLanguage(
        code = "Telugu",
        displayName = "Telugu",
        nativeScript = "తెలుగు",
        popularArtists = "Devi Sri Prasad, Thaman S, Sid Sriram",
        emoji = "🎼"
    )
)
