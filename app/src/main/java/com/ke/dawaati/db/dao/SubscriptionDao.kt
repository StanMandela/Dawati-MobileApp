package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.SubscriptionDetailsEntity
import com.ke.dawaati.db.model.SubscriptionTypesEntity

@Dao
interface SubscriptionDao {
    @Insert
    fun insertSubscriptionTypes(subscriptionTypesEntity: SubscriptionTypesEntity)

    @Query("SELECT * FROM subscription_types")
    fun loadSubscriptionTypes(): List<SubscriptionTypesEntity>

    @Query("SELECT COUNT(id) FROM subscription_types")
    fun countSubscriptionTypes(): Int

    @Query("DELETE FROM subscription_types")
    fun deleteSubscriptionTypes()

    @Insert
    fun insertSubscriptionDetails(subscriptionDetailsEntity: SubscriptionDetailsEntity)

    @Query("SELECT * FROM subscription_details")
    fun loadSubscriptionDetails(): SubscriptionDetailsEntity

    @Query("SELECT COUNT(id) FROM subscription_details")
    fun countSubscriptionDetails(): Int

    @Query("DELETE FROM subscription_details")
    fun deleteSubscriptionDetails()
}
