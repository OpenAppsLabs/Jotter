/*
 * Copyright (c) 2026 Open Apps Labs
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
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openappslabs.jotter.data.repository.CategoryRepository
import com.openappslabs.jotter.data.repository.CategoryRepositoryImpl
import com.openappslabs.jotter.data.repository.NotesRepository
import com.openappslabs.jotter.data.repository.NotesRepositoryImpl
import com.openappslabs.jotter.data.source.CategoryDao
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
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_isArchived_isTrashed` ON `notes` (`isArchived`, `isTrashed`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_category` ON `notes` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_isPinned_updatedTime` ON `notes` (`isPinned`, `updatedTime`)")
            }
        }

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): JotterDatabase {
            return Room.databaseBuilder(
                context,
                JotterDatabase::class.java,
                "jotter_db"
            )
                .addMigrations(MIGRATION_4_5)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        val prefs = context.getSharedPreferences("jotter_internal", Context.MODE_PRIVATE)
                        val count = prefs.getInt("archive_migration_runs", 0)
                        if (count < 3) {
                            db.execSQL("UPDATE notes SET isArchived = 0 WHERE isArchived = 1")
                            prefs.edit().putInt("archive_migration_runs", count + 1).apply()
                        }
                    }
                })
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