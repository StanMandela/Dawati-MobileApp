package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.ResumeEbooksEntity
import com.ke.dawaati.db.model.ResumeVideosEntity

class ResumeRepository(application: Application) {

    private val resumeDao = DawatiDatabase.getDatabase(application).resumeDao()

    fun insertResumeVideos(resumeVideosEntity: ResumeVideosEntity) {
        resumeDao.insertResumeVideos(resumeVideosEntity = resumeVideosEntity)
    }

    fun loadResumeVideos() = resumeDao.loadResumeVideos()

    fun countExistingResumeVideos(file_id: String) = resumeDao.countExistingResumeVideos(file_id = file_id)

    fun countResumeVideos() = resumeDao.countResumeVideos()

    fun deleteLastResumeVideos() { resumeDao.deleteLastResumeVideos() }

    fun deleteResumeVideoByFileID(file_id: String) {
        resumeDao.deleteResumeVideoByFileID(file_id = file_id)
    }

    fun deleteResumeVideos() {
        resumeDao.deleteResumeVideos()
    }

    /**
     * Ebook resume
     **/
    fun insertResumeEbooks(resumeEbooksEntity: ResumeEbooksEntity) {
        resumeDao.insertResumeEbooks(resumeEbooksEntity = resumeEbooksEntity)
    }

    fun loadResumeEbooks() = resumeDao.loadResumeEbooks()

    fun deleteResumeEbooks() {
        resumeDao.deleteResumeEbooks()
    }
}
