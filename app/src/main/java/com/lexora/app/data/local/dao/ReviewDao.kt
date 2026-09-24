package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.MistakeEntity
import com.lexora.app.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insertReview(review: ReviewEntity): Long

    @Query("SELECT COUNT(*) FROM reviews WHERE date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    fun getReviewCountByDate(date: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews WHERE isCorrect = 1 AND date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    fun getCorrectReviewCountByDate(date: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews WHERE date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getReviewCountByDateSync(date: Long): Int

    @Query("SELECT COUNT(*) FROM reviews WHERE isCorrect = 1 AND date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getCorrectReviewCountByDateSync(date: Long): Int

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert
    suspend fun insertMistake(mistake: MistakeEntity): Long

    @Query("SELECT * FROM mistakes ORDER BY createdAt DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT DISTINCT m.wordId FROM mistakes m INNER JOIN words w ON m.wordId = w.id WHERE m.wordId IS NOT NULL")
    fun getMistakeWordIds(): Flow<List<Long>>

    @Query("DELETE FROM mistakes WHERE wordId = :wordId")
    suspend fun deleteMistakesByWordId(wordId: Long)

    @Query("DELETE FROM mistakes WHERE grammarId = :grammarId")
    suspend fun deleteMistakesByGrammarId(grammarId: Long)

    @Query("DELETE FROM mistakes WHERE noteId = :noteId")
    suspend fun deleteMistakesByNoteId(noteId: Long)

    @Query("DELETE FROM mistakes WHERE wordId IS NOT NULL AND wordId NOT IN (SELECT id FROM words)")
    suspend fun deleteOrphanedMistakes(): Int

    @Delete
    suspend fun deleteMistake(mistake: MistakeEntity)

    @Query("SELECT COUNT(DISTINCT wordId) FROM mistakes WHERE wordId IS NOT NULL")
    fun getMistakeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews")
    fun getTotalReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews WHERE isCorrect = 1")
    fun getCorrectReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM mistakes WHERE questionType = 'review' OR questionType = 'review_grammar' OR questionType = 'review_note'")
    fun getReviewMistakeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM mistakes WHERE questionType LIKE '%EN_TO_FA%' OR questionType LIKE '%FA_TO_EN%'")
    fun getQuizMistakeCount(): Flow<Int>
}
