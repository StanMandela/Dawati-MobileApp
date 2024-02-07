package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumeVideos")
data class ResumeVideosEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "indexID") var indexID: String = "",
    @ColumnInfo(name = "subtopicID") var subtopicID: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "availability") var availability: String = "",
    @ColumnInfo(name = "file_name") var file_name: String = "",
    @ColumnInfo(name = "views") var views: String = "",
    @ColumnInfo(name = "file_id") var file_id: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = "",
    @ColumnInfo(name = "seekPosition") var seekPosition: String = ""
)

@Entity(tableName = "resumeEbooks")
data class ResumeEbooksEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "topicID") var topicID: String = "",
    @ColumnInfo(name = "subtopicID") var subtopicID: String = "",
    @ColumnInfo(name = "file_name") var file_name: String = "",
    @ColumnInfo(name = "file_type") var file_type: String = "",
    @ColumnInfo(name = "file_id") var file_id: String = "",
    @ColumnInfo(name = "multimedia_series") var multimedia_series: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "availability") var availability: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = ""
)
