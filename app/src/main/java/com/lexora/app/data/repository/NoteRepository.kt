package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.NoteDao
import com.lexora.app.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)

    fun getNoteByIdFlow(id: Long): Flow<NoteEntity?> = noteDao.getNoteByIdFlow(id)

    fun getFavoriteNotes(): Flow<List<NoteEntity>> = noteDao.getFavoriteNotes()

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    fun getNoteCount(): Flow<Int> = noteDao.getNoteCount()

    suspend fun getLeitnerNotesDue(currentTime: Long): List<NoteEntity> = noteDao.getLeitnerNotesDue(currentTime)

    fun getLeitnerNotes(): Flow<List<NoteEntity>> = noteDao.getLeitnerNotes()

    fun getAllCategories(): Flow<List<String>> = noteDao.getAllCategories()

    suspend fun updateCategoryName(oldName: String, newName: String) = noteDao.updateCategoryName(oldName, newName)
}
