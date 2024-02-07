package com.ke.dawaati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_types")
data class SubscriptionTypesEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "subscriptionID") var subscriptionID: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "amount") var amount: String = ""
)

@Entity(tableName = "subscription_details")
data class SubscriptionDetailsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "indexID") var indexID: String = "",
    @ColumnInfo(name = "userID") var userID: String = "",
    @ColumnInfo(name = "mPesaTransactionID") var mPesaTransactionID: String = "",
    @ColumnInfo(name = "subscriptionType") var subscriptionType: String = "",
    @ColumnInfo(name = "status") var status: String = "",
    @ColumnInfo(name = "startDate") var startDate: String = "",
    @ColumnInfo(name = "expiry") var expiry: String = ""
)
