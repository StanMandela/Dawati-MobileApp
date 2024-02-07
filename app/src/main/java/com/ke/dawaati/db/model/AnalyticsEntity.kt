package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics")
data class AnalyticsEntity(
    @PrimaryKey
    @ColumnInfo(name = "analyticsID") var analyticsID: String,
    @ColumnInfo(name = "startStamp") var startStamp: String,
    @ColumnInfo(name = "endStamp") var endStamp: String,
    @ColumnInfo(name = "contentType") var contentType: String,
    @ColumnInfo(name = "contentID") var contentID: String,
    @ColumnInfo(name = "internetType") var internetType: String,
    @ColumnInfo(name = "phoneType") var phonetype: String,
    @ColumnInfo(name = "syncStatus") var syncStatus: Boolean = false
)
