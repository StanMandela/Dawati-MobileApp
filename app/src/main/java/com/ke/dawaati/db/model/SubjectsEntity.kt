package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "subjectID") var subjectID: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "logo") var logo: String = ""
)

@Entity(tableName = "levels")
data class LevelsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "level_code") var level_code: String = "",
    @ColumnInfo(name = "level_name") var level_name: String = ""
)
