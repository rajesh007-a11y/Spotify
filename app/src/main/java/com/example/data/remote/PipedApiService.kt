package com.example.data.remote

import com.example.data.remote.model.PipedSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PipedApiService {
    @GET("search")
    suspend fun searchYouTube(
        @Query("q") query: String,
        @Query("filter") filter: String = "music_songs"
    ): Response<PipedSearchResponse>

    @GET("streams/{videoId}")
    suspend fun getStreams(
        @retrofit2.http.Path("videoId") videoId: String
    ): Response<com.example.data.remote.model.PipedStreamsResponse>

    companion object {
        private val PIPED_INSTANCES = listOf(
            "https://pipedapi.kavin.rocks/",
            "https://pipedapi.tokhmi.xyz/",
            "https://piped-api.lunar.icu/"
        )

        fun create(baseUrl: String = PIPED_INSTANCES.first()): PipedApiService {
            return buildService(baseUrl)
        }

        fun createWithFallback(): PipedApiServiceWithFallback {
            return PipedApiServiceWithFallback(PIPED_INSTANCES.map { buildService(it) })
        }

        private fun buildService(baseUrl: String): PipedApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(PipedApiService::class.java)
        }
    }
}

/**
 * Wrapper that tries multiple Piped API instances in sequence,
 * falling back to the next one if the current one fails.
 */
class PipedApiServiceWithFallback(private val instances: List<PipedApiService>) {
    suspend fun searchYouTube(query: String, filter: String = "music_songs"): Response<PipedSearchResponse> {
        var lastException: Exception? = null
        for (instance in instances) {
            try {
                val response = instance.searchYouTube(query, filter)
                if (response.isSuccessful) return response
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: Exception("All Piped instances failed")
    }

    suspend fun getStreams(videoId: String): Response<com.example.data.remote.model.PipedStreamsResponse> {
        var lastException: Exception? = null
        for (instance in instances) {
            try {
                val response = instance.getStreams(videoId)
                if (response.isSuccessful) return response
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: Exception("All Piped instances failed")
    }
}
