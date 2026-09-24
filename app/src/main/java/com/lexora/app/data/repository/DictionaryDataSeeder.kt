package com.lexora.app.data.repository

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lexora.app.data.local.LexoraDatabase
import com.lexora.app.data.local.dao.DictSource
import com.lexora.app.data.local.dao.DictionaryDao
import com.lexora.app.data.local.entity.DictionaryEntryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.lexora.app.utils.EnglishPhonetics
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Singleton

data class SeedStatus(
    val isSeeding: Boolean = false,
    val message: String = "",
    val inserted: Int = 0,
    val expected: Int = 0,
    val totalWords: Int = 0,
    val failed: Boolean = false
) {
    val progress: Float
        get() = if (expected <= 0) 0f else (inserted.toFloat() / expected).coerceIn(0f, 1f)
}

@Singleton
class DictionaryDataSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: LexoraDatabase,
    private val dictionaryDao: DictionaryDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow(SeedStatus())
    val status: StateFlow<SeedStatus> = _status.asStateFlow()

    companion object {
        private const val TAG = "DictSeeder"
        private const val PREFS = "lexora_dict_seed"
        private const val KEY_SEED_VERSION = "seed_version"

        
        const val SEED_VERSION = 9

        private const val BIGDICT_ASSET = "lexora_bigdict.csv"
        private const val CURATED_ASSET = "lexora_dictionary.csv"

        private const val EXPECTED_BIG_WORDS = 302_497
        private const val MIN_BIG_WORDS = 100_000
        private const val MIN_CURATED_WORDS = 700
        
        
        private const val COMMIT_EVERY = 2_000

        private const val PERSIAN_JOINER = "\u061B "

        private const val INSERT_SQL =
            "INSERT OR IGNORE INTO dictionary_entries (" +
                "englishWord, englishPhonetic, englishDefinition, englishPartOfSpeech, " +
                "persianWord, persianPhonetic, persianDefinition, persianPartOfSpeech, " +
                "synonyms, antonyms, example, exampleTranslation, wordType, level, tags, " +
                "isFavorite, createdAt, updatedAt" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    }

    fun seedDictionaryData() {
        if (_status.value.isSeeding) return
        scope.launch { runSeed() }
    }

    private suspend fun runSeed() {
        _status.value = SeedStatus(isSeeding = true, message = "Initializing dictionary...")
        try {
                        delay(1000)

            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val doneVersion = prefs.getInt(KEY_SEED_VERSION, 0)

                        database.openHelper.writableDatabase

            val curatedCount = runCatching { dictionaryDao.getCuratedCount() }.getOrDefault(0)
            val bigCount = runCatching { dictionaryDao.getBigDictCount() }.getOrDefault(0)
            val currentFormatBig = runCatching { dictionaryDao.countBySource(DictSource.BIGDICT) }.getOrDefault(0)
            
            val needCurated = curatedCount < MIN_CURATED_WORDS
            val needBig = bigCount < MIN_BIG_WORDS || currentFormatBig < MIN_BIG_WORDS

            if (!needCurated && !needBig && doneVersion >= SEED_VERSION) {
                val total = runCatching { dictionaryDao.getWordCount() }.getOrDefault(0)
                _status.value = SeedStatus(isSeeding = false, totalWords = total, message = "Ready")
                return
            }

            if (needCurated) {
                _status.value = _status.value.copy(message = "Loading curated words...")
                seedCurated()
            }

            if (needBig) {
                _status.value = _status.value.copy(message = "Loading 300k words...", inserted = 0, expected = EXPECTED_BIG_WORDS)
                
                                var deleted = false
                repeat(3) { 
                    if (!deleted) {
                        try {
                            dictionaryDao.deleteBigDict()
                            deleted = true
                        } catch (e: Exception) {
                            delay(500)
                        }
                    }
                }
                
                val total = streamBigDict()
                Log.i(TAG, "Bulk seeding finished: $total words")
            }

            val finalCount = runCatching { dictionaryDao.getWordCount() }.getOrDefault(0)
            prefs.edit().putInt(KEY_SEED_VERSION, SEED_VERSION).apply()
            _status.value = SeedStatus(isSeeding = false, totalWords = finalCount, message = "Offline Dictionary Ready")
        } catch (e: Exception) {
            Log.e(TAG, "Dictionary seeding failed", e)
            val total = runCatching { dictionaryDao.getWordCount() }.getOrDefault(0)
            _status.value = SeedStatus(
                isSeeding = false, 
                totalWords = total, 
                failed = true, 
                message = "Error: ${e.javaClass.simpleName}. Tap to retry."
            )
        }
    }

    private suspend fun seedCurated() {
        try {
            val result = mutableListOf<DictionaryEntryEntity>()
            context.assets.open(CURATED_ASSET).bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    if (line.isBlank() || line.startsWith("#")) return@forEachLine
                    val parts = line.split('|')
                    if (parts.size < 12) return@forEachLine
                    
                    val word = parts[0].trim().lowercase()
                    if (word.isEmpty()) return@forEachLine
                    
                    result += DictionaryEntryEntity(
                        englishWord = word,
                        englishPhonetic = parts[1].trim(),
                        englishPartOfSpeech = parts[2].trim(),
                        englishDefinition = parts[3].trim(),
                        persianWord = parts[4].trim(),
                        persianDefinition = parts[5].trim(),
                        synonyms = parts[6].trim(),
                        antonyms = parts[7].trim(),
                        example = parts[8].trim(),
                        exampleTranslation = parts[9].trim(),
                        wordType = DictSource.CURATED,
                        level = parts[10].trim().ifBlank { "A1" },
                        tags = parts[11].trim()
                    )
                }
            }
            dictionaryDao.insertAllIfAbsent(result)
        } catch (e: Exception) {
            Log.e(TAG, "Curated seeding failed", e)
        }
    }

    private suspend fun streamBigDict(): Int = withContext(Dispatchers.IO) {
        var total = 0
        var sinceCommit = 0
        val db = database.openHelper.writableDatabase

        try {
                                    
            val stmt = db.compileStatement(INSERT_SQL)
            val now = System.currentTimeMillis()

            val assetStream = context.assets.open(BIGDICT_ASSET)
            
                        val buffer = ByteArray(1024)
            val bytesRead = assetStream.read(buffer)
            
                        val isGzip = bytesRead >= 2 && buffer[0] == 0x1F.toByte() && buffer[1] == 0x8B.toByte()
            
                        assetStream.close()
            val freshStream = context.assets.open(BIGDICT_ASSET)
            
            val inputStream = if (isGzip) GZIPInputStream(freshStream) else freshStream
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

            var inTransaction = false
            
            try {
                reader.lineSequence().forEach { line ->
                    if (line.isBlank()) return@forEach
                    
                    if (!inTransaction) {
                                                var started = false
                        repeat(5) {
                            if (!started) {
                                try {
                                    db.beginTransactionNonExclusive()
                                    started = true
                                    inTransaction = true
                                } catch (e: Exception) {
                                    Thread.sleep(100)
                                }
                            }
                        }
                    }

                    try {
                        val parts = line.split('\t').filter { it.isNotBlank() }
                        if (parts.size < 2) return@forEach

                        val en = parts[0].trim().lowercase()
                        val rawSenses = parts.last()
                        val pos = if (parts.size >= 3) mapPos(parts[1]) else ""
                        
                        val senses = rawSenses.split(';').map { it.trim() }.filter { it.isNotBlank() }
                        if (senses.isEmpty()) return@forEach

                        val phonetic = EnglishPhonetics.toIPA(en)

                        stmt.clearBindings()
                        stmt.bindString(1, en)
                        stmt.bindString(2, phonetic)
                        stmt.bindString(3, "")
                        stmt.bindString(4, pos)
                        stmt.bindString(5, senses.first())
                        stmt.bindString(6, "")
                        stmt.bindString(7, senses.joinToString(PERSIAN_JOINER))
                        stmt.bindString(8, pos)
                        stmt.bindString(9, "")
                        stmt.bindString(10, "")
                        stmt.bindString(11, "")
                        stmt.bindString(12, "")
                        stmt.bindString(13, DictSource.BIGDICT)
                        stmt.bindString(14, "")
                        stmt.bindString(15, "bigdict")
                        stmt.bindLong(16, 0L)
                        stmt.bindLong(17, now)
                        stmt.bindLong(18, now)
                        stmt.executeInsert()

                        total++
                        sinceCommit++

                        if (sinceCommit >= COMMIT_EVERY) {
                            db.setTransactionSuccessful()
                            db.endTransaction()
                            inTransaction = false
                            sinceCommit = 0
                            _status.value = _status.value.copy(inserted = total)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Line error: ${e.message}")
                    }
                }
                
                if (inTransaction) {
                    db.setTransactionSuccessful()
                }
            } finally {
                if (inTransaction) db.endTransaction()
                reader.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Bulk streaming failed", e)
            throw e
        }
        total
    }

    private fun mapPos(pos: String): String = when (pos.lowercase().trim()) {
        "n", "noun" -> "noun"
        "v", "verb" -> "verb"
        "adj", "adjective" -> "adjective"
        "adv", "adverb" -> "adverb"
        "pron", "pronoun" -> "pronoun"
        "prep", "preposition" -> "preposition"
        "conj", "conjunction" -> "conjunction"
        else -> ""
    }

    fun reseed() {
        if (_status.value.isSeeding) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SEED_VERSION, 0).apply()
        seedDictionaryData()
    }

    suspend fun refreshWordCount() {
        if (_status.value.isSeeding) return
        val total = runCatching { dictionaryDao.getWordCount() }.getOrDefault(0)
        _status.value = _status.value.copy(totalWords = total)
    }
}
