package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.UserDetailsEntity

@Dao
interface UserDetailsDao {
    @Insert
    fun insertUserDetails(userDetailsEntity: UserDetailsEntity)

    @Query("SELECT * FROM user_details")
    fun loadUserDetails(): UserDetailsEntity?

    @Query("SELECT COUNT(id) FROM user_details")
    fun countUserDetails(): Int

    @Query("DELETE FROM user_details")
    fun deleteUserDetails()
}
