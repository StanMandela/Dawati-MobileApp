package com.ke.dawaati.uiux

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.util.AnalyticConstants
import com.ke.dawaati.util.getCurrentTimeStamp
import com.ke.dawaati.util.getModel
import com.ke.dawaati.util.getNetworkType
import com.ke.dawaati.util.getTimeStamp

class MainViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private var analyticsID: String = "ana${getTimeStamp()}"

    fun loadAnalytics() {
        val analytics = analyticsRepository.syncAnalytics()
        println(analytics)
    }

    fun createSessionAnalytics(context: Context) {
        analyticsRepository.insertAnalytics(
            analyticsEntity = AnalyticsEntity(
                analyticsID = analyticsID,
                startStamp = getCurrentTimeStamp(),
                endStamp = AnalyticConstants.EMPTY,
                contentType = AnalyticConstants.SESSION,
                contentID = AnalyticConstants.EMPTY,
                internetType = getNetworkType(context = context),
                phonetype = getModel()
            )
        )
        val analytics = analyticsRepository.syncAnalytics()
    }

    fun closeSessionAnalytics() {
        analyticsRepository.updateAnalytics(
            endStamp = getCurrentTimeStamp(),
            analyticsID = analyticsID
        )
    }
}
