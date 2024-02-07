package com.ke.dawaati.db.repo

import android.app.Application
import com.ke.dawaati.db.DawatiDatabase
import com.ke.dawaati.db.model.EBooksEntity
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.db.model.TopicsEntity

class ContentRepository(application: Application) {

    private val videosDao = DawatiDatabase.getDatabase(application).videosDao()
    private val eBooksDao = DawatiDatabase.getDatabase(application).eBooksDao()

    fun insertTopics(topicsEntity: TopicsEntity) {
        videosDao.insertTopics(topicsEntity = topicsEntity)
    }

    fun loadTopics(topicCategory: String, subject_id: String, level_id: String): List<TopicsEntity> {
        return videosDao.loadTopics(topicCategory = topicCategory, subject_id = subject_id, level_id = level_id)
    }

    fun countTopics(): Int {
        return videosDao.countTopics()
    }

    fun deleteTopics(topicID: String) {
        return videosDao.deleteTopics(topicID = topicID)
    }

    fun deleteTopics() {
        videosDao.deleteTopics()
    }

    /**
     * Sub topics
     */
    fun insertSubTopics(subTopicsEntity: SubTopicsEntity) {
        videosDao.insertSubTopics(subTopicsEntity = subTopicsEntity)
    }

    fun loadSubTopics(topicCategory: String, subject_id: String, level_id: String): List<SubTopicsEntity> {
        return videosDao.loadSubTopics(topicCategory = topicCategory, subject_id = subject_id, level_id = level_id)
    }

    fun loadSubTopicsByFileID(file_id: String): SubTopicsEntity? {
        return videosDao.loadSubTopicsByFileID(file_id = file_id)
    }

    fun countSubTopics(): Int {
        return videosDao.countSubTopics()
    }

    fun deleteSubTopics(topicID: String, file_id: String) {
        return videosDao.deleteSubTopics(topicID = topicID, file_id = file_id)
    }

    fun deleteSubTopics() {
        videosDao.deleteSubTopics()
    }

    // region Practical videos
    fun insertPracticals(practicalsEntity: PracticalsEntity) {
        videosDao.insertPracticals(practicalsEntity = practicalsEntity)
    }

    fun loadPracticals(subject_id: String, level_id: String) =
        videosDao.loadPracticals(subject_id = subject_id, level_id = level_id)

    fun loadPracticalsByFileID(file_id: String) = videosDao.loadPracticalsByFileID(file_id = file_id)

    fun countPracticals() = videosDao.countPracticals()

    fun deletePracticals(file_id: String) {
        videosDao.deletePracticals(file_id = file_id)
    }

    fun deletePracticals() {
        videosDao.deletePracticals()
    }
    // endregion

    /**
     * EBooks
     */
    fun insertEBooks(eBooksEntity: EBooksEntity) {
        eBooksDao.insertEBooks(eBooksEntity = eBooksEntity)
    }

    fun loadEBooks(
        ebook_category: String,
        subject_id: String,
        level_id: String
    ): List<EBooksEntity> {
        return eBooksDao.loadEBooks(
            ebook_category = ebook_category,
            subject_id = subject_id,
            level_id = level_id
        )
    }

    fun loadEBooksByID(id: Int): EBooksEntity {
        return eBooksDao.loadEBooksByID(id = id)
    }

    fun countEBooks(): Int {
        return eBooksDao.countEBooks()
    }

    fun deleteEBooks(file_id: String) {
        eBooksDao.deleteEBooks(file_id = file_id)
    }

    fun deleteEBooks() {
        eBooksDao.deleteEBooks()
    }
}
