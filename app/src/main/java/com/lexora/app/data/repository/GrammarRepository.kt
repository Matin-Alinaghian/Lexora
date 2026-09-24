package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.GrammarDao
import com.lexora.app.data.local.entity.GrammarEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrammarRepository @Inject constructor(
    private val grammarDao: GrammarDao
) {
    fun getAllGrammar(): Flow<List<GrammarEntity>> = grammarDao.getAllGrammar()

    suspend fun getGrammarById(id: Long): GrammarEntity? = grammarDao.getGrammarById(id)

    fun getGrammarByIdFlow(id: Long): Flow<GrammarEntity?> = grammarDao.getGrammarByIdFlow(id)

    fun getFavoriteGrammar(): Flow<List<GrammarEntity>> = grammarDao.getFavoriteGrammar()

    fun searchGrammar(query: String): Flow<List<GrammarEntity>> = grammarDao.searchGrammar(query)

    suspend fun insertGrammar(grammar: GrammarEntity): Long = grammarDao.insertGrammar(grammar)

    suspend fun updateGrammar(grammar: GrammarEntity) = grammarDao.updateGrammar(grammar)

    suspend fun deleteGrammar(grammar: GrammarEntity) = grammarDao.deleteGrammar(grammar)

    suspend fun deleteGrammarById(id: Long) = grammarDao.deleteGrammarById(id)

    fun getGrammarCount(): Flow<Int> = grammarDao.getGrammarCount()

    suspend fun getLeitnerGrammarDue(currentTime: Long): List<GrammarEntity> = grammarDao.getLeitnerGrammarDue(currentTime)

    fun getLeitnerGrammar(): Flow<List<GrammarEntity>> = grammarDao.getLeitnerGrammar()

    fun getAllCategories(): Flow<List<String>> = grammarDao.getAllCategories()

    suspend fun updateCategoryName(oldName: String, newName: String) = grammarDao.updateCategoryName(oldName, newName)
}
