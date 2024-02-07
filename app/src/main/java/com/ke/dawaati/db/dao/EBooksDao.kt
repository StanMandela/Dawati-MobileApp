package com.ke.dawaati.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ke.dawaati.db.model.EBooksEntity

@Dao
interface EBooksDao {
    @Insert
    fun insertEBooks(eBooksEntity: EBooksEntity)

    @Query("SELECT * FROM ebooks WHERE ebook_category =:ebook_category AND subject_id =:subject_id AND level_id =:level_id")
    fun loadEBooks(ebook_category: String, subject_id: String, level_id: String): List<EBooksEntity>

    @Query("SELECT * FROM ebooks WHERE id =:id")
    fun loadEBooksByID(id: Int): EBooksEntity

    @Query("SELECT COUNT(id) FROM ebooks")
    fun countEBooks(): Int

    @Query("DELETE FROM ebooks WHERE file_id =:file_id")
    fun deleteEBooks(file_id: String)

    @Query("DELETE FROM ebooks")
    fun deleteEBooks()
}
