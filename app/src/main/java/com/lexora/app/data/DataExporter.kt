package com.lexora.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lexora.app.data.local.entity.*
import com.lexora.app.data.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordRepository: WordRepository,
    private val grammarRepository: GrammarRepository,
    private val noteRepository: NoteRepository,
    private val studySessionRepository: StudySessionRepository,
    private val streakRepository: StreakRepository
) {
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .setPrettyPrinting()
        .create()

    data class ExportData(
        val exportDate: String,
        val appVersion: String,
        val words: List<WordEntity>,
        val grammars: List<GrammarEntity>,
        val notes: List<NoteEntity>,
        val studySessions: List<StudySessionEntity>,
        val streakInfo: StreakInfoExportDto
    )

    data class StreakInfoExportDto(
        val currentStreak: Int,
        val longestStreak: Int,
        val activeDaysCount: Int
    )

    
    suspend fun exportData(appVersion: String = "1.0.0"): File? = withContext(Dispatchers.IO) {
        try {
            val words = wordRepository.getAllWords().first()
            val grammars = grammarRepository.getAllGrammar().first()
            val notes = noteRepository.getAllNotes().first()
            val studySessions = studySessionRepository.getAllSessions().first()
            val currentStreak = streakRepository.getCurrentStreak().first()
            val longestStreak = streakRepository.getLongestStreak()
            val activeDays = streakRepository.getActiveDaysCount()

            val exportData = ExportData(
                exportDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .format(Date()),
                appVersion = appVersion,
                words = words,
                grammars = grammars,
                notes = notes,
                studySessions = studySessions,
                streakInfo = StreakInfoExportDto(
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    activeDaysCount = activeDays
                )
            )

            val json = gson.toJson(exportData)
            val fileName = "lexora_export_${System.currentTimeMillis()}.json"
            val file = File(context.getExternalFilesDir(null), fileName)

            file.writeText(json)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    
    suspend fun importData(file: File): ImportResult = withContext(Dispatchers.IO) {
        try {
            val json = file.readText()
            val exportData = gson.fromJson(json, ExportData::class.java)

            var importedWords = 0
            var importedGrammars = 0
            var importedNotes = 0
            var importedSessions = 0
            val errors = mutableListOf<String>()

                        exportData.words.forEach { word ->
                try {
                    wordRepository.insertWord(word.copy(id = 0))
                    importedWords++
                } catch (e: Exception) {
                    errors.add("Failed to import word '${word.englishWord}': ${e.message}")
                }
            }

                        exportData.grammars.forEach { grammar ->
                try {
                    grammarRepository.insertGrammar(grammar.copy(id = 0))
                    importedGrammars++
                } catch (e: Exception) {
                    errors.add("Failed to import grammar '${grammar.title}': ${e.message}")
                }
            }

                        exportData.notes.forEach { note ->
                try {
                    noteRepository.insertNote(note.copy(id = 0))
                    importedNotes++
                } catch (e: Exception) {
                    errors.add("Failed to import note '${note.title}': ${e.message}")
                }
            }
            
                        exportData.studySessions.forEach { session ->
                try {
                    studySessionRepository.recordSession(session.copy(id = 0))
                    importedSessions++
                } catch (e: Exception) {
                    errors.add("Failed to import session from '${session.date}': ${e.message}")
                }
            }

            ImportResult(
                success = errors.isEmpty(),
                importedWords = importedWords,
                importedGrammars = importedGrammars,
                importedNotes = importedNotes,
                importedSessions = importedSessions,
                errors = errors
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                importedWords = 0,
                importedGrammars = 0,
                importedNotes = 0,
                importedSessions = 0,
                errors = listOf("Failed to parse file: ${e.message}")
            )
        }
    }
}

data class ImportResult(
    val success: Boolean,
    val importedWords: Int,
    val importedGrammars: Int,
    val importedNotes: Int,
    val importedSessions: Int,
    val errors: List<String>
)
