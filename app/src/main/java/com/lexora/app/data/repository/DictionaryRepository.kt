package com.lexora.app.data.repository

import com.lexora.app.ai.LocalDictionary
import com.lexora.app.ai.model.WordAiResult
import com.lexora.app.data.local.dao.DictSource
import com.lexora.app.data.local.dao.DictionaryDao
import com.lexora.app.data.local.entity.DictionaryEntryEntity
import com.lexora.app.data.remote.dictionary.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(
    private val dictionaryDao: DictionaryDao
) {

    private val freeDictApi = DictionaryApiClient.freeDictApi
    private val farsiMatrixApi = DictionaryApiClient.farsiMatrixApi
    private val genericApi = DictionaryApiClient.genericApi

    
    fun searchDictionary(query: String): Flow<List<DictionaryResult>> = flow {
        val trimmedQuery = query.trim().lowercase()
        if (trimmedQuery.isBlank()) {
            emit(emptyList())
            return@flow
        }

        val results = mutableListOf<DictionaryResult>()

                                        val localDbEntries = try {
            val english = dictionaryDao.searchByEnglish(trimmedQuery)
            val persian = if (english.size < 8) {
                dictionaryDao.searchByPersian(trimmedQuery)
            } else {
                emptyList()
            }
            (english + persian).distinctBy { it.englishWord.lowercase() }
        } catch (e: Exception) {
            android.util.Log.w("DictRepo", "Local dictionary lookup failed", e)
            emptyList()
        }
        localDbEntries.forEach { entry ->
            val dbResult = mapEntityToDictionaryResult(entry)
            if (results.none { it.word.lowercase() == dbResult.word.lowercase() }) {
                results.add(dbResult)
            }
        }
        if (results.isNotEmpty()) {
            android.util.Log.d("DictRepo", "Local hits for '$trimmedQuery': ${results.size}")
            emit(results.toList())
        }

                if (results.isEmpty()) {
            LocalDictionary.getWord(trimmedQuery)?.let {
                results.add(mapLocalAiResultToDictionaryResult(it))
                emit(results.toList())
            }
        }

                try {
            val onlineResults = fetchFromBothApis(trimmedQuery)
            if (onlineResults.isNotEmpty()) {
                onlineResults.forEach { online ->
                    val index = results.indexOfFirst { it.word.lowercase() == online.word.lowercase() }
                    if (index != -1) {
                                                val existing = results[index]
                        results[index] = online.copy(
                            persianTranslation = online.persianTranslation
                                .ifBlank { existing.persianTranslation },
                            persianMeanings = online.persianMeanings
                                .ifEmpty { existing.persianMeanings },
                            level = online.level.ifBlank { existing.level }
                        )
                    } else {
                        results.add(online)
                    }
                }

                                val best = onlineResults.first()
                if (best.persianTranslation.isNotBlank()) {
                    addEntry(mapDictionaryResultToEntity(best))
                }
                emit(results.toList())
            } else if (results.isEmpty()) {
                emit(emptyList())
            }
        } catch (e: Exception) {
            android.util.Log.w("DictRepo", "Online lookup failed for '$trimmedQuery'", e)
            if (results.isEmpty()) emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    private fun mapLocalAiResultToDictionaryResult(ai: WordAiResult): DictionaryResult {
        val persian = ai.meanings.map { it.text }.filter { it.isNotBlank() }
        return DictionaryResult(
            word = ai.word,
            phonetic = ai.pronunciation,
            audioUrl = "",
            partOfSpeech = ai.wordType,
            persianMeanings = persian,
            definitions = listOf(DefinitionItem(ai.wordType, ai.meanings.firstOrNull()?.text ?: "", ai.example)),
            synonyms = ai.synonyms,
            antonyms = ai.antonyms,
            level = ai.level,
            examples = listOf(ai.example).filter { it.isNotBlank() },
            persianTranslation = persian.firstOrNull() ?: ai.exampleTranslation
        )
    }

    private fun mapEntityToDictionaryResult(entity: DictionaryEntryEntity): DictionaryResult {
                        val senses = entity.persianDefinition
            .split('\u061B', ';', '\n')
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val persianMeanings = (listOf(entity.persianWord) + senses)
            .filter { it.isNotBlank() }
            .distinct()

        return DictionaryResult(
            word = entity.englishWord,
            phonetic = entity.englishPhonetic,
            audioUrl = "",
            partOfSpeech = entity.englishPartOfSpeech,
            definitions = listOf(
                DefinitionItem(entity.englishPartOfSpeech, entity.englishDefinition, entity.example)
            ).filter { it.definition.isNotBlank() },
            synonyms = entity.synonyms.split(",").filter { it.isNotBlank() },
            antonyms = entity.antonyms.split(",").filter { it.isNotBlank() },
            level = entity.level,
            examples = listOf(entity.example).filter { it.isNotBlank() && it.trim() != "-" },
            persianTranslation = entity.persianWord.ifBlank { senses.firstOrNull() ?: "" },
            persianMeanings = persianMeanings
        )
    }

    private fun mapDictionaryResultToEntity(res: DictionaryResult): DictionaryEntryEntity {
        val persian = if (res.persianMeanings.isNotEmpty()) {
            res.persianMeanings.joinToString(PERSIAN_JOINER)
        } else {
            res.persianTranslation
        }
        return DictionaryEntryEntity(
            englishWord = res.word.lowercase(),
            englishPhonetic = res.phonetic,
            englishDefinition = res.definitions.firstOrNull()?.definition ?: "",
            englishPartOfSpeech = res.partOfSpeech,
            persianWord = res.persianTranslation.ifBlank { res.persianMeanings.firstOrNull() ?: "" },
            persianDefinition = persian,
            example = res.examples.firstOrNull() ?: "",
            level = res.level,
            synonyms = res.synonyms.joinToString(","),
            antonyms = res.antonyms.joinToString(","),
            wordType = DictSource.ONLINE,
            createdAt = System.currentTimeMillis()
        )
    }

    
    private suspend fun fetchFromBothApis(query: String): List<DictionaryResult> = coroutineScope {
        val englishDeferred = async {
            try {
                fetchEnglishDefinition(query)
            } catch (e: Exception) {
                android.util.Log.e("DictRepo", "English API error for '$query'", e)
                emptyList()
            }
        }

        val persianDeferred = async {
            try {
                fetchPersianTranslation(query)
            } catch (e: Exception) {
                android.util.Log.e("DictRepo", "Persian API error for '$query'", e)
                emptyMap<String, String>()
            }
        }

        val englishResults = englishDeferred.await()
        val persianMap = persianDeferred.await()
        android.util.Log.d("DictRepo", "English results: ${englishResults.size}, Persian translations: ${persianMap.size}")

        englishResults.map { result ->
            val persian = persianMap[result.word.lowercase()] ?: ""
            result.copy(
                persianTranslation = result.persianTranslation.ifBlank { persian },
                persianMeanings = result.persianMeanings.ifEmpty {
                    listOf(persian).filter { it.isNotBlank() }
                }
            )
        }
    }

    
    private suspend fun fetchEnglishDefinition(word: String): List<DictionaryResult> {
        return try {
            val response = freeDictApi.getEnglishDefinition(
                "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
            )

            android.util.Log.d("DictRepo", "FreeDict API for '$word': ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.map { apiResult ->
                    val phonetic = apiResult.phonetic
                        ?: apiResult.phonetics?.firstOrNull { it.text != null }?.text
                        ?: ""

                    val audioUrl = apiResult.phonetics
                        ?.firstOrNull { !it.audio.isNullOrBlank() }?.audio
                        ?: ""

                    val allMeanings = apiResult.meanings ?: emptyList()
                    val firstMeaning = allMeanings.firstOrNull()

                    val definitions = allMeanings.flatMap { meaning ->
                        meaning.definitions.map { def ->
                            DefinitionItem(
                                partOfSpeech = meaning.partOfSpeech,
                                definition = def.definition,
                                example = def.example ?: ""
                            )
                        }
                    }

                    val synonyms = allMeanings.flatMap { it.synonyms ?: emptyList() }.distinct()
                    val antonyms = allMeanings.flatMap { it.antonyms ?: emptyList() }.distinct()

                    val examples = allMeanings.flatMap { meaning ->
                        meaning.definitions.mapNotNull { it.example }
                    }.distinct()

                    val level = estimateLevel(word, definitions.size)

                    DictionaryResult(
                        word = apiResult.word,
                        phonetic = phonetic,
                        audioUrl = audioUrl,
                        partOfSpeech = firstMeaning?.partOfSpeech ?: "",
                        definitions = definitions.take(5),
                        synonyms = synonyms.take(8),
                        antonyms = antonyms.take(8),
                        level = level,
                        examples = examples.take(3)
                    )
                } ?: emptyList()
            } else {
                android.util.Log.w("DictRepo", "FreeDict API failed for '$word': ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("DictRepo", "FreeDict API exception for '$word'", e)
            emptyList()
        }
    }

    
    private suspend fun fetchPersianTranslation(word: String): Map<String, String> {
                try {
            val response = farsiMatrixApi.getFarsiTranslation(word)
            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()
                android.util.Log.d("DictRepo", "FarsiMatrix '$word': ${results.size} results")
                val map = results.associate { result ->
                    (result.word ?: "").lowercase() to (result.translation ?: "")
                }.filterValues { it.isNotBlank() }
                if (map.isNotEmpty()) return map
            } else {
                android.util.Log.w("DictRepo", "FarsiMatrix failed for '$word': ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.w("DictRepo", "FarsiMatrix unreachable for '$word'")
        }

                try {
            val url = "https://api.mymemory.translated.net/get?q=$word&langpair=en|fa"
            val response = genericApi.getMyMemoryTranslation(url)
            if (response.isSuccessful) {
                val translated = response.body()?.responseData?.translatedText
                if (!translated.isNullOrBlank() && !translated.contains("MYMEMORY WARNING", ignoreCase = true)) {
                    android.util.Log.d("DictRepo", "MyMemory '$word': $translated")
                    return mapOf(word.lowercase() to translated)
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("DictRepo", "MyMemory unreachable for '$word'")
        }

        return emptyMap()
    }

    
    private fun estimateLevel(word: String, definitionCount: Int): String {
        val length = word.length
        val hasMultipleSyllables = word.count { it in "aeiou" } > 3

        return when {
            length <= 4 && definitionCount <= 2 -> "A1"
            length <= 6 && !hasMultipleSyllables -> "A2"
            length <= 8 && definitionCount <= 3 -> "B1"
            length <= 10 || hasMultipleSyllables -> "B2"
            length <= 13 -> "C1"
            else -> "C2"
        }
    }

    
    fun getAllLocalEntries(): Flow<List<DictionaryEntryEntity>> =
        dictionaryDao.getAllEntries()

    fun searchLocalEntries(query: String): Flow<List<DictionaryEntryEntity>> =
        dictionaryDao.searchEntries(query)

    suspend fun getEntryById(id: Long): DictionaryEntryEntity? =
        dictionaryDao.getEntryById(id)

    suspend fun getLocalWordCount(): Int =
        runCatching { dictionaryDao.getWordCount() }.getOrDefault(0)

    suspend fun addEntry(entry: DictionaryEntryEntity): Long =
        dictionaryDao.insert(entry.copy(id = 0))

    suspend fun updateEntry(entry: DictionaryEntryEntity) =
        dictionaryDao.update(entry)

    suspend fun deleteEntry(entry: DictionaryEntryEntity) =
        dictionaryDao.delete(entry)

    companion object {
        
        const val PERSIAN_JOINER = "\u061B "
    }
}
