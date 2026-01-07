package com.example.planner.data.repository

import com.example.planner.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun createCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryById(id: Long): Category?
    fun getAllCategories(): Flow<List<Category>>
    fun getDefaultCategories(): Flow<List<Category>>
    suspend fun initializeDefaultCategories()
}
