package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ebooks")
data class EBooksEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "file_id") var file_id: String = "",
    @ColumnInfo(name = "file_name") var file_name: String = "",
    @ColumnInfo(name = "multimedia_series") var multimedia_series: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = "",
    @ColumnInfo(name = "topic_id") var topic_id: String = "",
    @ColumnInfo(name = "sub_topic_id") var sub_topic_id: String = "",
    @ColumnInfo(name = "file_type") var file_type: String = "",
    @ColumnInfo(name = "availability") var availability: String = "",
    @ColumnInfo(name = "ebook_category") var ebook_category: String = "",
    @ColumnInfo(name = "subject_id") var subject_id: String = "",
    @ColumnInfo(name = "subject_name") var subject_name: String = "",
    @ColumnInfo(name = "level_id") var level_id: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = ""
)
