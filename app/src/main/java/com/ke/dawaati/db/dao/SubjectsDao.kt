package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.db.model.SubjectsEntity

@Dao
interface SubjectsDao {
    @Insert
    fun insertLevels(levelsEntity: LevelsEntity)

    @Query("SELECT * FROM levels")
    fun loadLevels(): List<LevelsEntity>

    @Query("SELECT COUNT(id) FROM levels")
    fun countLevels(): Int

    @Query("DELETE FROM levels")
    fun deleteLevels()

    /**
     * Subjects DAO functions
     */
    @Insert
    fun insertSubjects(subjectsEntity: SubjectsEntity)

    @Query("SELECT * FROM subjects")
    fun loadSubjects(): List<SubjectsEntity>

    @Query("SELECT COUNT(id) FROM subjects")
    fun countSubjects(): Int

    @Query("DELETE FROM subjects")
    fun deleteSubjects()
}
