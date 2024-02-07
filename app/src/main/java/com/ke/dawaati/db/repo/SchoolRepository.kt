package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.SchoolEntity

class SchoolRepository(application: Application) {

    private val schoolDao = DawatiDatabase.getDatabase(application).schoolDao()

    fun insertSchool(schoolEntity: SchoolEntity) {
        schoolDao.insertSchool(schoolEntity = schoolEntity)
    }

    fun loadSchool(): List<SchoolEntity> {
        return schoolDao.loadSchool()
    }

    fun countSchool(): Int {
        return schoolDao.countSchool()
    }

    fun deleteSchool() {
        schoolDao.deleteSchool()
    }
}
