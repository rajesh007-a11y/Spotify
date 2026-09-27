package com.example.data.remote

import com.example.BuildConfig
import com.example.data.remote.model.SaavnSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface JioSaavnApiService {

    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<SaavnSearchResponse>

    @GET("api/songs/{id}/suggestions")
    suspend fun getSimilarSongs(
        @Path("id") id: String,
        @Query("limit") limit: Int = 10
    ): Response<com.example.data.remote.model.SaavnSuggestionsResponse>

    companion object {
        private const val DEFAULT_BASE_URL = "https://jiosaavn-api-jnlz.onrender.com/"

        fun create(
            apiKey: String = try { BuildConfig.JIOSAAVN_API_KEY } catch (_: Exception) { "" },
            baseUrl: String = try { BuildConfig.JIOSAAVN_BASE_URL } catch (_: Exception) { DEFAULT_BASE_URL }
        ): JioSaavnApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)

            // Inject API key header if provided in .env
            if (apiKey.isNotBlank() && apiKey != "YOUR_JIOSAAVN_API_KEY") {
                clientBuilder.addInterceptor { chain ->
                    val original = chain.request()
                    val requestWithAuth = original.newBuilder()
                        .header("Authorization", "Bearer $apiKey")
                        .header("X-API-Key", apiKey)
                        .build()
                    chain.proceed(requestWithAuth)
                }
            }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

            return Retrofit.Builder()
                .baseUrl(sanitizedUrl)
                .client(clientBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(JioSaavnApiService::class.java)
        }
    }
}
