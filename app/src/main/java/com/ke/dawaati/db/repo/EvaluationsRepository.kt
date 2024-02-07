package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.db.model.EvaluationsEntity

class EvaluationsRepository(application: Application) {

    private val evaluationsDao = DawatiDatabase.getDatabase(application).evaluationsDao()

    fun insertEvaluations(evaluationsEntity: EvaluationsEntity) {
        evaluationsDao.insertEvaluations(evaluationsEntity = evaluationsEntity)
    }

    fun loadEvaluations(subject_id: String): List<EvaluationsEntity> {
        return evaluationsDao.loadEvaluations(subject_id = subject_id)
    }

    fun countEvaluations(subject_id: String): Int {
        return evaluationsDao.countEvaluations(subject_id = subject_id)
    }

    fun deleteEvaluations(subject_id: String) {
        evaluationsDao.deleteEvaluations(subject_id = subject_id)
    }

    fun deleteEvaluations() {
        evaluationsDao.deleteEvaluations()
    }

    /**
     *
     */
    fun insertEvaluationAttempts(evaluationsAttemptsEntity: EvaluationsAttemptsEntity) {
        evaluationsDao.insertEvaluationAttempts(evaluationsAttemptsEntity = evaluationsAttemptsEntity)
    }

    fun loadEvaluationAttempts(): List<EvaluationsAttemptsEntity> {
        return evaluationsDao.loadEvaluationAttempts()
    }

    fun deleteEvaluationAttempts() {
        evaluationsDao.deleteEvaluationAttempts()
    }
}
