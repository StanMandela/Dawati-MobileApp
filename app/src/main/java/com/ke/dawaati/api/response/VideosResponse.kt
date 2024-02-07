package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideosResponse(
    @Json(name = "result")
    val result: VideoResult? = null
)

@JsonClass(generateAdapter = true)
data class VideoResult(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "topics")
    val topics: List<VideoTopics> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VideoTopics(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "topicID")
    val topicID: String = "",
    @Json(name = "subtopics")
    val subtopics: List<VideoSubTopics> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VideoSubTopics(
    @Json(name = "indexID")
    val indexID: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "subtopicID")
    val subtopicID: String = "",
    @Json(name = "availability")
    val availability: String = "",
    @Json(name = "file_name")
    val file_name: String = "",
    @Json(name = "views")
    val views: String = "",
    @Json(name = "file_id")
    val file_id: String = "",
    @Json(name = "subject")
    val subject: String = "",
    @Json(name = "file_url")
    val file_url: String = "",
    @Json(name = "seekPosition")
    val seekPosition: String = ""
)

// region practical videos
@JsonClass(generateAdapter = true)
data class PracticalsResponse(
    @Json(name = "result")
    val result: PracticalsResult? = null
)

@JsonClass(generateAdapter = true)
data class PracticalsResult(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "practicals")
    val practicals: List<PracticalVideos> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PracticalVideos(
    @Json(name = "topicID")
    val topicID: String = "",
    @Json(name = "file_type")
    val file_type: String = "",
    @Json(name = "file_name")
    val file_name: String = "",
    @Json(name = "file_id")
    val file_id: String = "",
    @Json(name = "subtopicID")
    val subtopicID: String = "",
    @Json(name = "subject")
    val subject: String = "",
    @Json(name = "availability")
    val availability: String = "",
    @Json(name = "file_url")
    val file_url: String = ""
)
// endregion
