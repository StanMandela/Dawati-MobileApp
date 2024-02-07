package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.db.model.SubscriptionDetailsEntity
import com.ke.dawaati.db.model.SubscriptionTypesEntity
import com.ke.dawaati.db.model.UserDetailsEntity

class UserDetailsRepository(application: Application) {

    private val userDetailsDao = DawatiDatabase.getDatabase(application).userDetailsDao()
    private val subscriptionDao = DawatiDatabase.getDatabase(application).subscriptionDao()
    private val subjectsDao = DawatiDatabase.getDatabase(application).subjectsDao()

    fun insertUserDetails(userDetailsEntity: UserDetailsEntity) {
        userDetailsDao.insertUserDetails(userDetailsEntity = userDetailsEntity)
    }

    fun loadUserDetails() = userDetailsDao.loadUserDetails()

    fun countUserDetails() = userDetailsDao.countUserDetails()

    fun deleteUserDetails() {
        userDetailsDao.deleteUserDetails()
    }

    fun insertSubscriptionTypes(subscriptionTypesEntity: SubscriptionTypesEntity) {
        subscriptionDao.insertSubscriptionTypes(subscriptionTypesEntity = subscriptionTypesEntity)
    }

    fun loadSubscriptionTypes() = subscriptionDao.loadSubscriptionTypes()

    fun countSubscriptionTypes(): Int {
        return subscriptionDao.countSubscriptionTypes()
    }

    fun deleteSubscriptionTypes() {
        subscriptionDao.deleteSubscriptionTypes()
    }

    fun insertSubscriptionDetails(subscriptionDetailsEntity: SubscriptionDetailsEntity) {
        subscriptionDao.insertSubscriptionDetails(subscriptionDetailsEntity = subscriptionDetailsEntity)
    }

    fun loadSubscriptionDetails(): SubscriptionDetailsEntity {
        return subscriptionDao.loadSubscriptionDetails()
    }

    fun countSubscriptionDetails(): Int {
        return subscriptionDao.countSubscriptionDetails()
    }

    fun deleteSubscriptionDetails() {
        subscriptionDao.deleteSubscriptionDetails()
    }

    /**
     * Levels DAO functions
     */
    fun insertLevels(levelsEntity: LevelsEntity) {
        subjectsDao.insertLevels(levelsEntity = levelsEntity)
    }

    fun loadLevels(): List<LevelsEntity> {
        return subjectsDao.loadLevels()
    }

    fun countLevels(): Int {
        return subjectsDao.countLevels()
    }

    fun deleteLevels() {
        subjectsDao.deleteLevels()
    }

    /**
     * Subjects DAO functions
     */
    fun insertSubjects(subjectsEntity: SubjectsEntity) {
        subjectsDao.insertSubjects(subjectsEntity = subjectsEntity)
    }

    fun loadSubjects(): List<SubjectsEntity> {
        return subjectsDao.loadSubjects()
    }

    fun countSubjects(): Int {
        return subjectsDao.countSubjects()
    }

    fun deleteSubjects() {
        subjectsDao.deleteSubjects()
    }
}
