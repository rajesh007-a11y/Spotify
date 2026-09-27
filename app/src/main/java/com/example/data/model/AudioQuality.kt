package com.example.data.model

enum class AudioQuality(
    val title: String,
    val bitrateLabel: String,
    val description: String,
    val tagColorHex: Long
) {
    NORMAL(
        title = "Normal",
        bitrateLabel = "128 kbps AAC",
        description = "Standard efficiency streaming, low data usage",
        tagColorHex = 0xFFB3B3B3
    ),
    HIGH(
        title = "High",
        bitrateLabel = "256 kbps AAC",
        description = "Crystal clear audio with balanced compression",
        tagColorHex = 0xFF1DB954
    ),
    LOSSLESS_HIFI(
        title = "Lossless Hi-Fi",
        bitrateLabel = "1411 kbps FLAC (24-bit/96kHz)",
        description = "Master studio sound, zero compression loss, studio acoustics",
        tagColorHex = 0xFFFFD700
    )
}

data class EqualizerPreset(
    val name: String,
    // 5-band gain in dB: [60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz]
    val bands: List<Float>
) {
    companion object {
        val FLAT = EqualizerPreset("Flat", listOf(0f, 0f, 0f, 0f, 0f))
        val BASS_BOOST = EqualizerPreset("Bass Boost", listOf(6f, 4f, 1f, 0f, -1f))
        val ELECTRONIC = EqualizerPreset("Electronic", listOf(5f, 3f, 0f, 2f, 4f))
        val ROCK = EqualizerPreset("Rock & Metal", listOf(4f, 2f, -1f, 2f, 5f))
        val VOCAL_BOOSTER = EqualizerPreset("Vocal Booster", listOf(-2f, 0f, 5f, 4f, 1f))
        val ACOUSTIC = EqualizerPreset("Acoustic / Warm", listOf(3f, 3f, 2f, 2f, 3f))
        val TREBLE_BOOST = EqualizerPreset("Treble Boost", listOf(-2f, -1f, 1f, 4f, 6f))

        val ALL_PRESETS = listOf(FLAT, BASS_BOOST, ELECTRONIC, ROCK, VOCAL_BOOSTER, ACOUSTIC, TREBLE_BOOST)
    }
}
