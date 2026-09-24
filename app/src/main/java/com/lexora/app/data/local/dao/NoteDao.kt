package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteByIdFlow(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT COUNT(*) FROM notes")
    fun getNoteCount(): Flow<Int>

    @Query("SELECT * FROM notes WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    suspend fun getLeitnerNotesDue(currentTime: Long): List<NoteEntity>

    @Query("SELECT COUNT(*) FROM notes WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime")
    suspend fun getLeitnerNotesDueCount(currentTime: Long): Int

    @Query("SELECT * FROM notes WHERE isInLeitner = 1")
    fun getLeitnerNotes(): Flow<List<NoteEntity>>

    @Query("SELECT DISTINCT category FROM notes WHERE category != ''")
    fun getAllCategories(): Flow<List<String>>

    @Query("UPDATE notes SET category = :newName WHERE category = :oldName")
    suspend fun updateCategoryName(oldName: String, newName: String)
}
