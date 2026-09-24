package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.MistakeDao
import com.lexora.app.data.local.entity.MistakeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MistakeRepository @Inject constructor(
    private val mistakeDao: MistakeDao
) {
    fun getAllMistakes(): Flow<List<MistakeEntity>> = mistakeDao.getAllMistakes()

    fun getRecentMistakes(limit: Int = 10): Flow<List<MistakeEntity>> =
        mistakeDao.getRecentMistakes(limit)

    suspend fun addMistake(
        wordId: Long? = null,
        grammarId: Long? = null,
        noteId: Long? = null,
        question: String,
        correctAnswer: String,
        userAnswer: String,
        questionType: String,
        quizId: Long? = null
    ): Long {
        val mistake = MistakeEntity(
            wordId = wordId,
            grammarId = grammarId,
            noteId = noteId,
            question = question,
            correctAnswer = correctAnswer,
            userAnswer = userAnswer,
            questionType = questionType,
            quizId = quizId,
            createdAt = System.currentTimeMillis()
        )
        return mistakeDao.insert(mistake)
    }

    suspend fun deleteMistake(mistake: MistakeEntity) = mistakeDao.delete(mistake)

    suspend fun deleteMistakeById(id: Long) = mistakeDao.deleteById(id)

    suspend fun deleteAllMistakes() = mistakeDao.deleteAll()

    suspend fun deleteMistakesForWord(wordId: Long) = mistakeDao.deleteByWordId(wordId)

    suspend fun deleteMistakesForGrammar(grammarId: Long) = mistakeDao.deleteByGrammarId(grammarId)

    suspend fun deleteMistakesForNote(noteId: Long) = mistakeDao.deleteByNoteId(noteId)

    suspend fun getTotalMistakeCount(): Int = mistakeDao.getTotalMistakeCount()
}
