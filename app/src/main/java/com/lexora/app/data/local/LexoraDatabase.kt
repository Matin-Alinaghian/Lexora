package com.lexora.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lexora.app.data.local.dao.*
import com.lexora.app.data.local.entity.*

@Database(
    entities = [
        WordEntity::class,
        GrammarEntity::class,
        NoteEntity::class,
        ReviewEntity::class,
        MistakeEntity::class,
        StudySessionEntity::class,
        DailyStreakEntity::class,
        OverallStreakEntity::class,
        DictionaryEntryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class LexoraDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun grammarDao(): GrammarDao
    abstract fun noteDao(): NoteDao
    abstract fun reviewDao(): ReviewDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun streakDao(): StreakDao
    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                                db.execSQL("ALTER TABLE grammar ADD COLUMN isInLeitner INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE grammar ADD COLUMN leitnerBox INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE grammar ADD COLUMN nextReviewDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE grammar ADD COLUMN xpEarned INTEGER NOT NULL DEFAULT 0")

                                db.execSQL("ALTER TABLE notes ADD COLUMN isInLeitner INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE notes ADD COLUMN leitnerBox INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE notes ADD COLUMN nextReviewDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE notes ADD COLUMN xpEarned INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_dict_english ON dictionary_entries(englishWord)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_dict_persian ON dictionary_entries(persianWord)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_dict_source ON dictionary_entries(wordType)")
            }
        }
    }
}
