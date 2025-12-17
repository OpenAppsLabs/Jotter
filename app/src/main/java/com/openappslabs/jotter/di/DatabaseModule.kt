/*
 * Copyright (c) 2025 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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

    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepositoryImpl: NotesRepositoryImpl
    ): NotesRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository


    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): JotterDatabase {
            return Room.databaseBuilder(
                context,
                JotterDatabase::class.java,
                "jotter_db" // Database file name
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideNoteDao(database: JotterDatabase): NoteDao {
            return database.noteDao()
        }

        @Provides
        fun provideCategoryDao(database: JotterDatabase): CategoryDao {
            return database.categoryDao()
        }
    }
}