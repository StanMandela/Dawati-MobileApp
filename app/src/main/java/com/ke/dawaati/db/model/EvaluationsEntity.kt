package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluation")
data class EvaluationsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "subject_id") var subject_id: String = "",
    @ColumnInfo(name = "subject_name") var subject_name: String = "",
    @ColumnInfo(name = "exam_id") var exam_id: String = "",
    @ColumnInfo(name = "exam_name") var exam_name: String = "",
    @ColumnInfo(name = "hours") var hours: String = "",
    @ColumnInfo(name = "minutes") var minutes: String = ""
)

@Entity(tableName = "evaluation_attempts")
data class EvaluationsAttemptsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "exam_id") var exam_id: String = "",
    @ColumnInfo(name = "exam_name") var exam_name: String = "",
    @ColumnInfo(name = "subject") var subject: String = "",
    @ColumnInfo(name = "user_score") var user_score: String = "",
    @ColumnInfo(name = "response_id") var response_id: String = ""
)
