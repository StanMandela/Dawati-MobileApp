package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EBooksResponse(
    @Json(name = "result")
    val result: EBooksResult? = null
)

@JsonClass(generateAdapter = true)
data class EBooksResult(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "books")
    val books: List<BooksTopics> = emptyList()
)

@JsonClass(generateAdapter = true)
data class BooksTopics(
    @Json(name = "topicID")
    val topicID: String = "",
    @Json(name = "subtopicID")
    val subtopicID: String = "",
    @Json(name = "file_name")
    val file_name: String = "",
    @Json(name = "file_type")
    val file_type: String = "",
    @Json(name = "file_id")
    val file_id: String = "",
    @Json(name = "multimedia_series")
    val multimedia_series: String = "",
    @Json(name = "subject")
    val subject: String = "",
    @Json(name = "availability")
    val availability: String = "",
    @Json(name = "file_url")
    val file_url: String = ""
)
