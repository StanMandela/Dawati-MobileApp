package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
data class UserDetailsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "user_id") var user_id: String = "",
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "mobile") var mobile: String = "",
    @ColumnInfo(name = "subscription_status") var subscription_status: String = "",
    @ColumnInfo(name = "user_type") var user_type: String = "",
    @ColumnInfo(name = "about_me") var about_me: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = "",
    @ColumnInfo(name = "prof_img") var prof_img: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = ""
)
