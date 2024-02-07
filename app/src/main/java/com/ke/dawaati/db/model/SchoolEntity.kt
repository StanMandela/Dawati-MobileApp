package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school")
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "schoolID") var schoolID: String = "",
    @ColumnInfo(name = "schoolName") var schoolName: String = ""
)
