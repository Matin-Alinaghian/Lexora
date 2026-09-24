package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.DictionaryEntryEntity
import kotlinx.coroutines.flow.Flow

object DictSource {
    const val CURATED = "curated"
    const val BIGDICT = "bigdict"
    const val ONLINE = "online"
    const val LEGACY = "en->fa"
}

@Dao
interface DictionaryDao {

    @Query("SELECT * FROM dictionary_entries ORDER BY englishWord ASC LIMIT 100")
    fun getAllEntries(): Flow<List<DictionaryEntryEntity>>

    @Query("SELECT * FROM dictionary_entries WHERE englishWord LIKE :query || '%' OR persianWord LIKE :query || '%' ORDER BY englishWord ASC LIMIT 20")
    fun searchEntries(query: String): Flow<List<DictionaryEntryEntity>>

    @Query("SELECT * FROM dictionary_entries WHERE englishWord = :word LIMIT 1")
    suspend fun getEntryByEnglishWord(word: String): DictionaryEntryEntity?

    @Query("SELECT * FROM dictionary_entries WHERE persianWord = :word LIMIT 1")
    suspend fun getEntryByPersianWord(word: String): DictionaryEntryEntity?

    @Query("SELECT * FROM dictionary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DictionaryEntryEntity?

    @Query("SELECT * FROM dictionary_entries WHERE isFavorite = 1 ORDER BY englishWord ASC")
    fun getFavoriteEntries(): Flow<List<DictionaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DictionaryEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DictionaryEntryEntity>)

    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(entries: List<DictionaryEntryEntity>): List<Long>

    @Query("SELECT * FROM dictionary_entries WHERE englishWord LIKE :query || '%' OR persianWord LIKE :query || '%' ORDER BY englishWord ASC LIMIT 15")
    suspend fun searchEntriesLike(query: String): List<DictionaryEntryEntity>

    
    @Query(
        """
        SELECT * FROM dictionary_entries
        WHERE englishWord = :query OR englishWord LIKE :query || '%'
        ORDER BY
            CASE WHEN englishWord = :query THEN 0 ELSE 1 END,
            CASE WHEN wordType = 'curated' THEN 0 ELSE 1 END,
            LENGTH(englishWord) ASC,
            englishWord ASC
        LIMIT :limit
        """
    )
    suspend fun searchByEnglish(query: String, limit: Int = 15): List<DictionaryEntryEntity>

    
    @Query(
        """
        SELECT * FROM dictionary_entries
        WHERE persianWord = :query OR persianWord LIKE :query || '%'
        ORDER BY
            CASE WHEN persianWord = :query THEN 0 ELSE 1 END,
            CASE WHEN wordType = 'curated' THEN 0 ELSE 1 END,
            LENGTH(englishWord) ASC
        LIMIT :limit
        """
    )
    suspend fun searchByPersian(query: String, limit: Int = 15): List<DictionaryEntryEntity>

    
    @Query(
        """
        SELECT * FROM dictionary_entries
        WHERE englishWord LIKE :query || '%'
           OR persianWord LIKE :query || '%'
           OR persianDefinition LIKE '%' || :query || '%'
        ORDER BY CASE
            WHEN englishWord = :query THEN 0
            WHEN englishWord LIKE :query || '%' THEN 1
            WHEN persianWord = :query THEN 2
            ELSE 3 END,
        LENGTH(englishWord) ASC
        LIMIT 25
        """
    )
    suspend fun searchAll(query: String): List<DictionaryEntryEntity>

    @Update
    suspend fun update(entry: DictionaryEntryEntity)

    @Delete
    suspend fun delete(entry: DictionaryEntryEntity)

    @Query("DELETE FROM dictionary_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM dictionary_entries")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM dictionary_entries")
    suspend fun getCount(): Int

    @Query("SELECT * FROM dictionary_entries WHERE englishWord LIKE :prefix || '%' ORDER BY englishWord ASC LIMIT :limit")
    fun getSuggestions(prefix: String, limit: Int = 10): Flow<List<DictionaryEntryEntity>>

    @Query("SELECT * FROM dictionary_entries WHERE englishWord = :word OR persianWord = :word LIMIT 1")
    suspend fun findEntry(word: String): DictionaryEntryEntity?

    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE englishWord != ''")
    suspend fun getWordCount(): Int

    
    @Transaction
    suspend fun insertBatch(entries: List<DictionaryEntryEntity>) {
        entries.forEach { insert(it) }
    }

    
    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE wordType = :source")
    suspend fun countBySource(source: String): Int

    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE wordType = 'bigdict' OR tags = 'bigdict'")
    suspend fun getBigDictCount(): Int

    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE wordType = 'curated'")
    suspend fun getCuratedCount(): Int

    
    @Query("DELETE FROM dictionary_entries WHERE wordType = 'bigdict' OR tags = 'bigdict'")
    suspend fun deleteBigDict()

    
    @Query("DELETE FROM dictionary_entries WHERE wordType = 'en->fa' AND tags != 'bigdict'")
    suspend fun deleteLegacyRows(): Int

    @Query("SELECT englishWord FROM dictionary_entries WHERE isFavorite = 1")
    suspend fun getFavoriteWords(): List<String>

    @Query("UPDATE dictionary_entries SET isFavorite = 1 WHERE englishWord = :word")
    suspend fun markFavorite(word: String)
}
