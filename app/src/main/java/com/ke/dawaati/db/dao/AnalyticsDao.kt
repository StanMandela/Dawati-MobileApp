package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.AnalyticsEntity

@Dao
interface AnalyticsDao {
    @Insert
    fun insertAnalytics(analyticsEntity: AnalyticsEntity?)

    @Query("SELECT * FROM analytics WHERE syncStatus ='0' AND endStamp !=''")
    fun syncAnalytics(): List<AnalyticsEntity>

    @Query("UPDATE analytics SET syncStatus ='1' WHERE analyticsID =:analyticsID")
    fun updateAnalyticsSent(analyticsID: String?)

    @Query("UPDATE analytics SET endStamp = :endStamp WHERE analyticsID =:analyticsID")
    fun updateAnalytics(endStamp: String, analyticsID: String)

    @Query("DELETE FROM analytics")
    fun deleteAnalytics()
}
