package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResetResponse(
    @Json(name = "status")
    val status: Boolean = false,
    @Json(name = "result")
    val result: String = ""
)
