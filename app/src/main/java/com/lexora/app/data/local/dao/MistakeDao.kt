package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.MistakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistakes ORDER BY createdAt DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE quizId = :quizId ORDER BY createdAt DESC")
    fun getMistakesForQuiz(quizId: Long): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMistakes(limit: Int): Flow<List<MistakeEntity>>

    @Query("SELECT COUNT(*) FROM mistakes")
    suspend fun getTotalMistakeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mistake: MistakeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mistakes: List<MistakeEntity>)

    @Delete
    suspend fun delete(mistake: MistakeEntity)

    @Query("DELETE FROM mistakes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM mistakes")
    suspend fun deleteAll()

    @Query("DELETE FROM mistakes WHERE wordId = :wordId")
    suspend fun deleteByWordId(wordId: Long)

    @Query("DELETE FROM mistakes WHERE grammarId = :grammarId")
    suspend fun deleteByGrammarId(grammarId: Long)

    @Query("DELETE FROM mistakes WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)
}
