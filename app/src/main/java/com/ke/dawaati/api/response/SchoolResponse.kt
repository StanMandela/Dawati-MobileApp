package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SchoolResponse(
    @Json(name = "result")
    val result: Schools? = null
)

@JsonClass(generateAdapter = true)
data class Schools(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "schools")
    val schools: List<SchoolTypes>? = null
)

@JsonClass(generateAdapter = true)
data class SchoolTypes(
    @Json(name = "school_code")
    val school_code: String = "",
    @Json(name = "name")
    val name: String = ""
)
