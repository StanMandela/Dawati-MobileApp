package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "topicID") var topicID: String = "",
    @ColumnInfo(name = "topicCategory") var topicCategory: String = "",
    @ColumnInfo(name = "subject_id") var subject_id: String = "",
    @ColumnInfo(name = "subject_name") var subject_name: String = "",
    @ColumnInfo(name = "level_id") var level_id: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = ""
)

@Entity(tableName = "sub_topics")
data class SubTopicsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "topicCategory") var topicCategory: String = "",
    @ColumnInfo(name = "topicID") var topicID: String = "",
    @ColumnInfo(name = "indexID") var indexID: String = "",
    @ColumnInfo(name = "subTopicID") var subTopicID: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "availability") var availability: String = "",
    @ColumnInfo(name = "file_name") var file_name: String = "",
    @ColumnInfo(name = "views") var views: String = "",
    @ColumnInfo(name = "file_id") var file_id: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = "",
    @ColumnInfo(name = "subject_id") var subject_id: String = "",
    @ColumnInfo(name = "subject_name") var subject_name: String = "",
    @ColumnInfo(name = "level_id") var level_id: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = ""
)

@Entity(tableName = "practicals")
data class PracticalsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "topicID") var topicID: String = "",
    @ColumnInfo(name = "file_type") var file_type: String = "",
    @ColumnInfo(name = "file_name") var file_name: String = "",
    @ColumnInfo(name = "file_id") var file_id: String = "",
    @ColumnInfo(name = "subtopicID") var subtopicID: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "availability") var availability: String = "",
    @ColumnInfo(name = "file_url") var file_url: String = "",
    @ColumnInfo(name = "subject_id") var subject_id: String = "",
    @ColumnInfo(name = "subject_name") var subject_name: String = "",
    @ColumnInfo(name = "level_id") var level_id: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = ""
)
