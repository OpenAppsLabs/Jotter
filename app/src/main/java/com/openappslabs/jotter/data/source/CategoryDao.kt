package com.openappslabs.jotter.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openappslabs.jotter.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // ✨ FIX: Get all categories, EXCLUDING the empty string ("") category.
    // This prevents the empty chip from appearing in the Home Screen filter bar.
    // In CategoryDao.kt
    @Query("SELECT * FROM categories WHERE name != ''")
    fun getAllCategories(): Flow<List<Category>>

    // Insert a new category. Ignore if the category name already exists.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    // Delete a category by name (using the primary key)
    @Query("DELETE FROM categories WHERE name = :name")
    suspend fun deleteCategoryByName(name: String)

    // ✨ NEW: Bulk Insert for Restore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    // ✨ NEW: Bulk Delete for Restore
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    // Snapshot for Backup
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSync(): List<Category>
}