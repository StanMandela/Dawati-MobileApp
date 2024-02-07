package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.AnalyticsEntity

class AnalyticsRepository(application: Application) {

    private val analyticsDao = DawatiDatabase.getDatabase(application).analyticsDao()

    fun insertAnalytics(analyticsEntity: AnalyticsEntity) {
        analyticsDao.insertAnalytics(analyticsEntity = analyticsEntity)
    }

    fun syncAnalytics(): List<AnalyticsEntity> {
        return analyticsDao.syncAnalytics()
    }

    fun updateAnalyticsSent(analyticsID: String) {
        analyticsDao.updateAnalyticsSent(analyticsID = analyticsID)
    }

    fun updateAnalytics(endStamp: String, analyticsID: String) {
        analyticsDao.updateAnalytics(endStamp = endStamp, analyticsID = analyticsID)
    }

    fun deleteAnalytics() {
        analyticsDao.deleteAnalytics()
    }
}
