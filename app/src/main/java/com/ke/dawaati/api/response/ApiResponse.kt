package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "message")
    val message: String = "",
    @Json(name = "token")
    val token: String = ""
)
