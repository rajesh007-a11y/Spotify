package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PipedSearchResponse(
    @Json(name = "items") val items: List<PipedItem>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PipedItem(
    @Json(name = "title") val title: String = "",
    @Json(name = "type") val type: String = "stream"
)
