package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordByIdFlow(id: Long): Flow<WordEntity?>

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE englishWord LIKE '%' || :query || '%' OR persianMeaning LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<WordEntity>

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    suspend fun getAllWordsOnce(): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)

    @Query("SELECT COUNT(*) FROM words")
    fun getWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isFavorite = 1")
    fun getFavoriteWordCount(): Flow<Int>

    @Query("SELECT * FROM words WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    suspend fun getLeitnerWordsDue(currentTime: Long): List<WordEntity>

    
    @Query("SELECT COUNT(*) FROM words WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime")
    suspend fun getLeitnerDueCount(currentTime: Long): Int

    
    @Query("SELECT COUNT(*) FROM words WHERE isInLeitner = 1")
    suspend fun getLeitnerTotalCount(): Int

    @Query("SELECT * FROM words WHERE isInLeitner = 1")
    fun getAllLeitnerWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isInLeitner = 1")
    suspend fun getAllLeitnerWordsOnce(): List<WordEntity>

    @Query("UPDATE words SET xpEarned = xpEarned + :xp WHERE id = :id")
    suspend fun incrementWordXp(id: Long, xp: Int)

    @Query("SELECT DISTINCT category FROM words WHERE category != ''")
    fun getAllCategories(): Flow<List<String>>

    @Query("UPDATE words SET category = :newName WHERE category = :oldName")
    suspend fun updateCategoryName(oldName: String, newName: String)

    @Query("SELECT * FROM words WHERE id IN (SELECT DISTINCT wordId FROM mistakes WHERE wordId IS NOT NULL)")
    suspend fun getWordsWithMistakes(): List<WordEntity>
}
