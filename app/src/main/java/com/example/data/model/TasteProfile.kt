package com.example.data.model

data class TasteProfile(
    val averageEnergy: Float = 0.78f,
    val averageValence: Float = 0.85f,
    val dominantGenre: String = "Hindi Romantic",
    val topGenres: List<GenreStat> = listOf(
        GenreStat("Hindi Romantic", 42),
        GenreStat("Bengali Romantic (বাংলা)", 30),
        GenreStat("Rabindra Sangeet Fusion", 16),
        GenreStat("Bollywood Dance", 12)
    ),
    val totalPlaysTracked: Int = 68,
    val skipRatePercentage: Int = 4,
    val favoriteBpmRange: String = "104 - 118 BPM",
    val primaryMoodSummary: String = "Soulful, melodious Hindi romance & Bengali nostalgia",
    val aiTrainedModelVersion: String = "Soundify-Neural-Taste-v3.5",
    val lastTrainedTimeFormatted: String = "Just now",
    val djIntroCommentary: String = "Namaskar! DJ Soundify in the mix. I noticed you've been having Arijit Singh, soulful Hindi melodies and Bengali classics on repeat. Let's dive right into this magical set!"
)

data class GenreStat(
    val genre: String,
    val percentage: Int
)

enum class DjVibe(val label: String, val emoji: String, val targetEnergy: Float, val targetValence: Float, val promptHint: String) {
    BALANCED("Personal Flow", "✨", 0.75f, 0.80f, "A perfect blend of your favorite Hindi & Bengali melodies"),
    DESI_ROMANCE("Bollywood Romance", "💖", 0.72f, 0.85f, "Soulful Hindi romance featuring Arijit Singh & Pritam"),
    BANGLA_MELODY("Bangla Gaan (বাংলা)", "🪕", 0.68f, 0.82f, "Emotional Bengali classics, Rabindra fusion & modern melodies"),
    HIGH_ENERGY("Bollywood Dance Party", "⚡", 0.94f, 0.92f, "Fast tempo, driving dhols & high-voltage Bollywood dance"),
    LATE_NIGHT("Late Night Melodies", "🌙", 0.58f, 0.55f, "Quiet Hindi Sufi acoustics & acoustic Bengali rain songs"),
    DEEP_FOCUS("Acoustic & Rabindra Chill", "🧠", 0.45f, 0.60f, "Smooth sitar, flute, lo-fi and acoustic Bengali instruments")
}
