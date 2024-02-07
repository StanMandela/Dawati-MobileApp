package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActivateResponse(
    @Json(name = "result")
    val result: RegisterResponseResult? = null
)

@JsonClass(generateAdapter = true)
data class ActivateResponseResult(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "message")
    val message: String = ""
)
