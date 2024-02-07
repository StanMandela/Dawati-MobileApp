package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.db.model.EvaluationsEntity

@Dao
interface EvaluationsDao {
    @Insert
    fun insertEvaluations(evaluationsEntity: EvaluationsEntity)

    @Query("SELECT * FROM evaluation WHERE subject_id =:subject_id")
    fun loadEvaluations(subject_id: String): List<EvaluationsEntity>

    @Query("SELECT COUNT(id) FROM evaluation WHERE subject_id =:subject_id")
    fun countEvaluations(subject_id: String): Int

    @Query("DELETE FROM evaluation WHERE subject_id =:subject_id")
    fun deleteEvaluations(subject_id: String)

    @Query("DELETE FROM evaluation")
    fun deleteEvaluations()

    /**
     * All evaluation attempts
     */
    @Insert
    fun insertEvaluationAttempts(evaluationsAttemptsEntity: EvaluationsAttemptsEntity)

    @Query("SELECT * FROM evaluation_attempts")
    fun loadEvaluationAttempts(): List<EvaluationsAttemptsEntity>

    @Query("DELETE FROM evaluation_attempts")
    fun deleteEvaluationAttempts()
}
