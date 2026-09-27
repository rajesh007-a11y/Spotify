package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.ListeningEventEntity
import com.example.data.model.DjVibe
import com.example.data.model.TasteProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiDjService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateDjCommentary(
        tasteProfile: TasteProfile,
        recentEvents: List<ListeningEventEntity>,
        vibe: DjVibe,
        timeOfDay: String
    ): DjGenerationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no real API key is supplied, generate intelligent dynamic commentary
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalHeuristicCommentary(tasteProfile, vibe, timeOfDay)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val topSongsStr = recentEvents.take(5).joinToString(", ") { "${it.songTitle} by ${it.artistName} (${it.genre})" }
            val promptText = """
                You are DJ Soundify, an enthusiastic, charismatic personal AI DJ like Spotify's DJ X.
                Speak directly to the user in 2-3 engaging, conversational spoken sentences.
                
                Listener Context:
                - Current Time of Day: $timeOfDay
                - Desired Vibe: ${vibe.label} (${vibe.promptHint})
                - Top Genres: ${tasteProfile.topGenres.joinToString { "${it.genre} (${it.percentage}%)" }}
                - Average Energy: ${(tasteProfile.averageEnergy * 100).toInt()}%
                - Average Mood Valence: ${(tasteProfile.averageValence * 100).toInt()}%
                - Recently played: $topSongsStr
                
                Respond in valid JSON format with two keys:
                "commentary": Your spoken intro as DJ Soundify (ready to be read out loud, sounding lively and natural),
                "moodSummary": A concise 4-word descriptor of their current music taste.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    val parsed = JSONObject(text)
                    val commentary = parsed.optString("commentary", "")
                    val mood = parsed.optString("moodSummary", tasteProfile.primaryMoodSummary)
                    if (commentary.isNotBlank()) {
                        return@withContext DjGenerationResult(
                            commentary = commentary,
                            moodSummary = mood,
                            isAiGenerated = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiDjService", "Error generating DJ content: ${e.message}", e)
        }

        return@withContext generateLocalHeuristicCommentary(tasteProfile, vibe, timeOfDay)
    }

    private fun generateLocalHeuristicCommentary(
        profile: TasteProfile,
        vibe: DjVibe,
        timeOfDay: String
    ): DjGenerationResult {
        val commentary = when (vibe) {
            DjVibe.DESI_ROMANCE ->
                "Namaste! DJ Soundify here. Nothing beats the pure magic of Bollywood romance this $timeOfDay. I've lined up Kesariya, Tum Hi Ho, and soulful Arijit Singh hits to warm your heart."
            DjVibe.BANGLA_MELODY ->
                "Nomoshkar! Shono, your Bengali music taste is truly unmatched. Here are timeless tunes from Nachiketa, Pratul Mukhopadhyay, and modern Bengali love anthems."
            DjVibe.HIGH_ENERGY ->
                "Bollywood party mode unlocked! Get ready for high-voltage dhol beats, Ghungroo rhythms, and non-stop dance energy to get you moving!"
            DjVibe.LATE_NIGHT ->
                "Good $timeOfDay. Soft Hindi Sufi guitars and gentle rain melodies to accompany your quiet thoughts. Let the music take over."
            DjVibe.DEEP_FOCUS ->
                "Dialing in tranquility. Gentle acoustic sitar, acoustic Rabindra sangeet harmonies, and serene chords for deep meditation and work."
            DjVibe.BALANCED ->
                "What's good! DJ Soundify taking over your mix this $timeOfDay. Based on your recent repeats in ${profile.dominantGenre} and Bengali favorites, here is a handpicked groove tailored directly to your taste."
        }

        val moodSummary = when (vibe) {
            DjVibe.DESI_ROMANCE -> "Soulful Bollywood romance & passion"
            DjVibe.BANGLA_MELODY -> "Nostalgic Bangla melodies & acoustic warmth"
            DjVibe.HIGH_ENERGY -> "Maximum Bollywood dance energy & beats"
            DjVibe.LATE_NIGHT -> "Late-night Sufi & melancholic serenity"
            DjVibe.DEEP_FOCUS -> "Peaceful acoustic & classical harmony"
            DjVibe.BALANCED -> profile.primaryMoodSummary
        }

        return DjGenerationResult(commentary = commentary, moodSummary = moodSummary, isAiGenerated = false)
    }
}

data class DjGenerationResult(
    val commentary: String,
    val moodSummary: String,
    val isAiGenerated: Boolean
)
