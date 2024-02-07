package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubscribeResponse(
    @Json(name = "result")
    val result: SubscribeResponseResult? = null
)

@JsonClass(generateAdapter = true)
data class SubscribeResponseResult(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "message")
    val message: String = ""
)
