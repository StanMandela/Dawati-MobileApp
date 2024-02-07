package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.ResumeEbooksEntity
import com.ke.dawaati.db.model.ResumeVideosEntity

@Dao
interface ResumeDao {
    @Insert
    fun insertResumeVideos(resumeVideosEntity: ResumeVideosEntity)

    @Query("SELECT * FROM resumeVideos")
    fun loadResumeVideos(): List<ResumeVideosEntity>

    @Query("SELECT COUNT(id) FROM resumeVideos WHERE file_id =:file_id")
    fun countExistingResumeVideos(file_id: String): Int

    @Query("SELECT COUNT(id) FROM resumeVideos")
    fun countResumeVideos(): Int

    @Query("DELETE FROM resumeVideos WHERE id = (SELECT id FROM resumeVideos ORDER BY id DESC LIMIT 1)")
    fun deleteLastResumeVideos()

    @Query("DELETE FROM resumeVideos WHERE file_id =:file_id")
    fun deleteResumeVideoByFileID(file_id: String)

    @Query("DELETE FROM resumeVideos")
    fun deleteResumeVideos()

    /**
     * Ebook resume
     **/
    @Insert
    fun insertResumeEbooks(resumeEbooksEntity: ResumeEbooksEntity)

    @Query("SELECT * FROM resumeEbooks")
    fun loadResumeEbooks(): List<ResumeEbooksEntity>

    @Query("DELETE FROM resumeEbooks")
    fun deleteResumeEbooks()
}
