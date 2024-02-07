package com.ke.dawaati.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "result")
    val result: Result? = null
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "status")
    val status: String = "false",
    @Json(name = "message")
    val message: String = "false",
    @Json(name = "user_details")
    val user_details: UserDetails? = null,
    @Json(name = "subscription_types")
    val subscription_types: List<SubscriptionTypes> = emptyList(),
    @Json(name = "subscription_details")
    val subscription_details: List<SubscriptionDetails> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UserDetails(
    @Json(name = "user_id")
    val user_id: String = "",
    @Json(name = "username")
    val username: String = "",
    @Json(name = "email")
    val email: String = "",
    @Json(name = "mobile")
    val mobile: String = "",
    @Json(name = "subscription_status")
    val subscription_status: String = "",
    @Json(name = "user_type")
    val user_type: String = "",
    @Json(name = "about_me")
    val about_me: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "level_name")
    val level_name: String = "",
    @Json(name = "prof_img")
    val prof_img: String = "",
    @Json(name = "file_url")
    val file_url: String = ""
)

@JsonClass(generateAdapter = true)
data class SubscriptionTypes(
    @Json(name = "subscription_id")
    val subscription_id: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "amount")
    val amount: String = ""
)

@JsonClass(generateAdapter = true)
data class SubscriptionDetails(
    @Json(name = "index_id")
    val index_id: String = "",
    @Json(name = "user_id")
    val user_id: String = "",
    @Json(name = "mpesa_transaction_id")
    val mpesa_transaction_id: String = "",
    @Json(name = "subscription_type")
    val subscription_type: String = "",
    @Json(name = "status")
    val status: String = "",
    @Json(name = "start_date")
    val start_date: String = "",
    @Json(name = "expiry")
    val expiry: String = ""
)
