package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.ReviewDao
import com.lexora.app.data.local.entity.MistakeEntity
import com.lexora.app.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao,
) {

    fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()

    fun getReviewCountByDate(date: Long = System.currentTimeMillis()): Flow<Int> =
        reviewDao.getReviewCountByDate(date)

    fun getCorrectReviewCountByDate(date: Long = System.currentTimeMillis()): Flow<Int> =
        reviewDao.getCorrectReviewCountByDate(date)

    suspend fun getTodayReviewCount(): Int {
        val today = System.currentTimeMillis()
        return reviewDao.getReviewCountByDateSync(today)
    }

    suspend fun getTodayCorrectCount(): Int {
        val today = System.currentTimeMillis()
        return reviewDao.getCorrectReviewCountByDateSync(today)
    }

    suspend fun insertReview(review: ReviewEntity): Long = reviewDao.insertReview(review)

    suspend fun insertMistake(mistake: MistakeEntity): Long = reviewDao.insertMistake(mistake)

    fun getAllMistakes(): Flow<List<MistakeEntity>> = reviewDao.getAllMistakes()

    fun getMistakeWordIds(): Flow<List<Long>> = reviewDao.getMistakeWordIds()

    fun getMistakeCount(): Flow<Int> = reviewDao.getMistakeCount()

    fun getTotalReviewCount(): Flow<Int> = reviewDao.getTotalReviewCount()

    fun getCorrectReviewCount(): Flow<Int> = reviewDao.getCorrectReviewCount()

    fun getReviewMistakeCount(): Flow<Int> = reviewDao.getReviewMistakeCount()

    fun getQuizMistakeCount(): Flow<Int> = reviewDao.getQuizMistakeCount()

    suspend fun deleteMistake(mistake: MistakeEntity) = reviewDao.deleteMistake(mistake)

    suspend fun deleteMistakesByWordId(wordId: Long) = reviewDao.deleteMistakesByWordId(wordId)

    suspend fun deleteMistakesByGrammarId(grammarId: Long) = reviewDao.deleteMistakesByGrammarId(grammarId)

    suspend fun deleteMistakesByNoteId(noteId: Long) = reviewDao.deleteMistakesByNoteId(noteId)

    suspend fun deleteOrphanedMistakes() = reviewDao.deleteOrphanedMistakes()
}
