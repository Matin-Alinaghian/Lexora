package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.WordDao
import com.lexora.app.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    suspend fun getWordById(id: Long): WordEntity? = wordDao.getWordById(id)

    fun getWordByIdFlow(id: Long): Flow<WordEntity?> = wordDao.getWordByIdFlow(id)

    fun getFavoriteWords(): Flow<List<WordEntity>> = wordDao.getFavoriteWords()

    fun searchWords(query: String): Flow<List<WordEntity>> = wordDao.searchWords(query)

    suspend fun getRandomWords(limit: Int): List<WordEntity> = wordDao.getRandomWords(limit)

    suspend fun insertWord(word: WordEntity): Long = wordDao.insertWord(word)

    suspend fun updateWord(word: WordEntity) = wordDao.updateWord(word)

    suspend fun deleteWord(word: WordEntity) = wordDao.deleteWord(word)

    suspend fun deleteWordById(id: Long) = wordDao.deleteWordById(id)

    fun getWordCount(): Flow<Int> = wordDao.getWordCount()

    fun getFavoriteWordCount(): Flow<Int> = wordDao.getFavoriteWordCount()

    suspend fun getLeitnerWordsDue(currentTime: Long): List<WordEntity> =
        wordDao.getLeitnerWordsDue(currentTime)

    fun getAllLeitnerWords(): Flow<List<WordEntity>> =
        wordDao.getAllLeitnerWords()

    suspend fun getAllLeitnerWordsOnce(): List<WordEntity> =
        wordDao.getAllLeitnerWordsOnce()

    suspend fun incrementWordXp(id: Long, xp: Int) =
        wordDao.incrementWordXp(id, xp)

    fun getAllCategories(): Flow<List<String>> = wordDao.getAllCategories()

    suspend fun updateCategoryName(oldName: String, newName: String) = wordDao.updateCategoryName(oldName, newName)

    suspend fun getWordsWithMistakes(): List<WordEntity> = wordDao.getWordsWithMistakes()

    
    suspend fun addAllToLeitner(): Int {
        val allWords = wordDao.getAllWordsOnce()
        val now = System.currentTimeMillis()
        val tomorrow = now + (24 * 60 * 60 * 1000L)
        var added = 0
        allWords.filter { !it.isInLeitner }.forEach { word ->
            wordDao.updateWord(word.copy(
                isInLeitner = true,
                leitnerBox = 1,
                nextReviewDate = tomorrow
            ))
            added++
        }
        return added
    }
}
