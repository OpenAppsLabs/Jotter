package com.openappslabs.jotter.di

import android.content.Context
import androidx.room.Room
import com.openappslabs.jotter.data.repository.CategoryRepository // New Import
import com.openappslabs.jotter.data.repository.CategoryRepositoryImpl // New Import
import com.openappslabs.jotter.data.repository.NotesRepository
import com.openappslabs.jotter.data.repository.NotesRepositoryImpl
import com.openappslabs.jotter.data.source.CategoryDao // New Import
import com.openappslabs.jotter.data.source.JotterDatabase
import com.openappslabs.jotter.data.source.NoteDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    // --- BINDINGS (Interface to Implementation) ---
    // Binds the Notes Repository Interface
    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepositoryImpl: NotesRepositoryImpl
    ): NotesRepository

    // ✨ NEW BINDING: Binds the Category Repository Interface
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository


    companion object {
        // --- PROVIDERS (Concrete Objects) ---

        // Provides the main database instance (Version 2)
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): JotterDatabase {
            return Room.databaseBuilder(
                context,
                JotterDatabase::class.java,
                "jotter_db" // Database file name
            )
                // ✨ FIX: Allows Room to destroy the version 1 database and create version 2
                .fallbackToDestructiveMigration()
                .build()
        }

        // Provides the DAO for Notes (Existing)
        @Provides
        fun provideNoteDao(database: JotterDatabase): NoteDao {
            return database.noteDao()
        }

        // ✨ NEW PROVIDER: Provides the DAO for Categories
        @Provides
        fun provideCategoryDao(database: JotterDatabase): CategoryDao {
            return database.categoryDao()
        }
    }
}