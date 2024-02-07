package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IndexResponse(
    @Json(name = "status")
    val status: Boolean = true,
    @Json(name = "result")
    val result: Results? = null
)

@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "levels")
    val levels: List<Levels> = emptyList(),
    @Json(name = "subjects")
    val subjects: List<Subjects> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Levels(
    @Json(name = "level_code")
    val level_code: String = "",
    @Json(name = "level_name")
    val level_name: String = ""
)

@JsonClass(generateAdapter = true)
data class Subjects(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "logo")
    val logo: String = ""
)
