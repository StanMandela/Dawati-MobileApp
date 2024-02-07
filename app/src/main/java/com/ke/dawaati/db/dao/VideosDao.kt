package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.db.model.TopicsEntity

@Dao
interface VideosDao {
    @Insert
    fun insertTopics(topicsEntity: TopicsEntity)

    @Query("SELECT * FROM topics WHERE topicCategory =:topicCategory AND subject_id =:subject_id AND level_id =:level_id")
    fun loadTopics(topicCategory: String, subject_id: String, level_id: String): List<TopicsEntity>

    @Query("SELECT COUNT(id) FROM topics")
    fun countTopics(): Int

    @Query("DELETE FROM topics WHERE topicID =:topicID")
    fun deleteTopics(topicID: String)

    @Query("DELETE FROM topics")
    fun deleteTopics()

    /**
     * Sub topics
     */
    @Insert
    fun insertSubTopics(subTopicsEntity: SubTopicsEntity)

    @Query("SELECT * FROM sub_topics WHERE topicCategory =:topicCategory AND subject_id =:subject_id AND level_id =:level_id")
    fun loadSubTopics(topicCategory: String, subject_id: String, level_id: String): List<SubTopicsEntity>

    @Query("SELECT * FROM sub_topics WHERE file_id =:file_id")
    fun loadSubTopicsByFileID(file_id: String): SubTopicsEntity

    @Query("SELECT COUNT(id) FROM sub_topics")
    fun countSubTopics(): Int

    @Query("DELETE FROM sub_topics WHERE topicID =:topicID AND file_id =:file_id")
    fun deleteSubTopics(topicID: String, file_id: String)

    @Query("DELETE FROM sub_topics")
    fun deleteSubTopics()

    /**
     * Sub topics
     */
    @Insert
    fun insertPracticals(practicalsEntity: PracticalsEntity)

    @Query("SELECT * FROM practicals WHERE subject_id =:subject_id AND level_id =:level_id")
    fun loadPracticals(subject_id: String, level_id: String): List<PracticalsEntity>

    @Query("SELECT * FROM practicals WHERE file_id =:file_id")
    fun loadPracticalsByFileID(file_id: String): PracticalsEntity?

    @Query("SELECT COUNT(id) FROM practicals")
    fun countPracticals(): Int

    @Query("DELETE FROM practicals WHERE file_id =:file_id")
    fun deletePracticals(file_id: String)

    @Query("DELETE FROM practicals")
    fun deletePracticals()
}
