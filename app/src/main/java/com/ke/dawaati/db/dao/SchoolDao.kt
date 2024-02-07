package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.SchoolEntity

@Dao
interface SchoolDao {
    @Insert
    fun insertSchool(schoolEntity: SchoolEntity)

    @Query("SELECT * FROM school")
    fun loadSchool(): List<SchoolEntity>

    @Query("SELECT COUNT(id) FROM school")
    fun countSchool(): Int

    @Query("DELETE FROM school")
    fun deleteSchool()
}
