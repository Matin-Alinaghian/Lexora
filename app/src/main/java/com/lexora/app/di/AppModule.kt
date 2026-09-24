package com.lexora.app.di

import android.content.Context
import androidx.room.Room
import com.lexora.app.data.local.LexoraDatabase
import com.lexora.app.data.local.dao.*
import com.lexora.app.data.repository.*
import com.lexora.app.data.DictionaryDataProvider
import com.lexora.app.data.DataExporter
import com.lexora.app.notification.ReminderScheduler
import com.lexora.app.utils.StreakManager
import com.lexora.app.ai.provider.AiServiceProvider
import com.lexora.app.ai.AiModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LexoraDatabase {
        return Room.databaseBuilder(
            context,
            LexoraDatabase::class.java,
            "lexora_database"
        ).addMigrations(LexoraDatabase.MIGRATION_2_3, LexoraDatabase.MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWordDao(database: LexoraDatabase): WordDao = database.wordDao()

    @Provides
    fun provideGrammarDao(database: LexoraDatabase): GrammarDao = database.grammarDao()

    @Provides
    fun provideNoteDao(database: LexoraDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideReviewDao(database: LexoraDatabase): ReviewDao = database.reviewDao()

    @Provides
    fun provideStudySessionDao(database: LexoraDatabase): StudySessionDao = database.studySessionDao()

    @Provides
    fun provideMistakeDao(database: LexoraDatabase): MistakeDao = database.mistakeDao()

    @Provides
    fun provideStreakDao(database: LexoraDatabase): StreakDao = database.streakDao()

    @Provides
    fun provideDictionaryDao(database: LexoraDatabase): DictionaryDao = database.dictionaryDao()

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }

    @Provides
    @Singleton
    fun provideGrammarRepository(grammarDao: GrammarDao): GrammarRepository {
        return GrammarRepository(grammarDao)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(reviewDao: ReviewDao): ReviewRepository {
        return ReviewRepository(reviewDao)
    }

    @Provides
    @Singleton
    fun provideStudySessionRepository(studySessionDao: StudySessionDao): StudySessionRepository {
        return StudySessionRepository(studySessionDao)
    }

    @Provides
    @Singleton
    fun provideMistakeRepository(mistakeDao: MistakeDao): MistakeRepository {
        return MistakeRepository(mistakeDao)
    }

    @Provides
    @Singleton
    fun provideStreakRepository(streakDao: StreakDao, @ApplicationContext context: Context): StreakRepository {
        return StreakRepository(context, streakDao)
    }

    @Provides
    @Singleton
    fun provideDictionaryDataSeeder(
        @ApplicationContext context: Context,
        database: LexoraDatabase,
        dictionaryDao: DictionaryDao
    ): DictionaryDataSeeder {
        return DictionaryDataSeeder(context, database, dictionaryDao)
    }

    
    @Provides
    @Singleton
    fun provideStreakManager(
        @ApplicationContext context: Context,
        streakRepository: StreakRepository,
        studySessionRepository: StudySessionRepository
    ): StreakManager {
        return StreakManager(context, streakRepository, studySessionRepository)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): ReminderScheduler {
        return ReminderScheduler(context)
    }

    @Provides
    @Singleton
    fun provideDataExporter(
        @ApplicationContext context: Context,
        wordRepository: WordRepository,
        grammarRepository: GrammarRepository,
        noteRepository: NoteRepository,
        studySessionRepository: StudySessionRepository,
        streakRepository: StreakRepository
    ): DataExporter {
        return DataExporter(context, wordRepository, grammarRepository, noteRepository, studySessionRepository, streakRepository)
    }

    @Provides
    @Singleton
    fun provideDictionaryRepository(
        dictionaryDao: DictionaryDao
    ): com.lexora.app.data.repository.DictionaryRepository {
        return com.lexora.app.data.repository.DictionaryRepository(dictionaryDao)
    }
}
