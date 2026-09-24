package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.GrammarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar ORDER BY createdAt DESC")
    fun getAllGrammar(): Flow<List<GrammarEntity>>

    @Query("SELECT * FROM grammar WHERE id = :id")
    suspend fun getGrammarById(id: Long): GrammarEntity?

    @Query("SELECT * FROM grammar WHERE id = :id")
    fun getGrammarByIdFlow(id: Long): Flow<GrammarEntity?>

    @Query("SELECT * FROM grammar WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteGrammar(): Flow<List<GrammarEntity>>

    @Query("SELECT * FROM grammar WHERE title LIKE '%' || :query || '%' OR explanation LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchGrammar(query: String): Flow<List<GrammarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammar(grammar: GrammarEntity): Long

    @Update
    suspend fun updateGrammar(grammar: GrammarEntity)

    @Delete
    suspend fun deleteGrammar(grammar: GrammarEntity)

    @Query("DELETE FROM grammar WHERE id = :id")
    suspend fun deleteGrammarById(id: Long)

    @Query("SELECT COUNT(*) FROM grammar")
    fun getGrammarCount(): Flow<Int>

    @Query("SELECT * FROM grammar WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    suspend fun getLeitnerGrammarDue(currentTime: Long): List<GrammarEntity>

    @Query("SELECT COUNT(*) FROM grammar WHERE isInLeitner = 1 AND nextReviewDate <= :currentTime")
    suspend fun getLeitnerGrammarDueCount(currentTime: Long): Int

    @Query("SELECT * FROM grammar WHERE isInLeitner = 1")
    fun getLeitnerGrammar(): Flow<List<GrammarEntity>>

    @Query("SELECT DISTINCT category FROM grammar WHERE category != ''")
    fun getAllCategories(): Flow<List<String>>

    @Query("UPDATE grammar SET category = :newName WHERE category = :oldName")
    suspend fun updateCategoryName(oldName: String, newName: String)
}
