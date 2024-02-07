package com.ke.dawaati.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ke.dawaati.db.dao.AnalyticsDao
import com.ke.dawaati.db.dao.EBooksDao
import com.ke.dawaati.db.dao.EvaluationsDao
import com.ke.dawaati.db.dao.ResumeDao
import com.ke.dawaati.db.dao.SchoolDao
import com.ke.dawaati.db.dao.SubjectsDao
import com.ke.dawaati.db.dao.SubscriptionDao
import com.ke.dawaati.db.dao.UserDetailsDao
import com.ke.dawaati.db.dao.VideosDao
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.model.EBooksEntity
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.db.model.EvaluationsEntity
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.ResumeEbooksEntity
import com.ke.dawaati.db.model.ResumeVideosEntity
import com.ke.dawaati.db.model.SchoolEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.db.model.SubscriptionDetailsEntity
import com.ke.dawaati.db.model.SubscriptionTypesEntity
import com.ke.dawaati.db.model.TopicsEntity
import com.ke.dawaati.db.model.UserDetailsEntity

@Database(
    entities = [
        AnalyticsEntity::class,
        SchoolEntity::class,
        UserDetailsEntity::class,
        SubscriptionDetailsEntity::class,
        SubscriptionTypesEntity::class,
        SubjectsEntity::class,
        LevelsEntity::class,
        TopicsEntity::class,
        SubTopicsEntity::class,
        EBooksEntity::class,
        EvaluationsEntity::class,
        EvaluationsAttemptsEntity::class,
        ResumeVideosEntity::class,
        ResumeEbooksEntity::class,
        PracticalsEntity::class
    ],
    version = 1
)
abstract class DawatiDatabase : RoomDatabase() {

    abstract fun analyticsDao(): AnalyticsDao
    abstract fun schoolDao(): SchoolDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun userDetailsDao(): UserDetailsDao
    abstract fun subjectsDao(): SubjectsDao
    abstract fun videosDao(): VideosDao
    abstract fun eBooksDao(): EBooksDao
    abstract fun evaluationsDao(): EvaluationsDao
    abstract fun resumeDao(): ResumeDao

    companion object {
        @Volatile
        private var INSTANCE: DawatiDatabase? = null

        fun getDatabase(context: Context): DawatiDatabase {
            var tempInstance = INSTANCE
            if (tempInstance == null) {
                tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    DawatiDatabase::class.java, "dawati_db"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return tempInstance
        }
    }
}
