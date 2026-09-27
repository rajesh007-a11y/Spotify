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

    companion object {
        fun create(): PipedApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl("https://pipedapi.kavin.rocks/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(PipedApiService::class.java)
        }
    }
}
