package com.example.planner.data.repository

import com.example.planner.data.local.dao.CategoryDao
import com.example.planner.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun createCategory(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.let { Category.fromEntity(it) }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { Category.fromEntity(it) }
        }
    }

    override fun getDefaultCategories(): Flow<List<Category>> {
        return categoryDao.getDefaultCategories().map { entities ->
            entities.map { Category.fromEntity(it) }
        }
    }

    override suspend fun initializeDefaultCategories() {
        val count = categoryDao.getCategoryCount()
        if (count == 0) {
            val defaults = Category.getDefaultCategories()
            categoryDao.insertAll(defaults.map { it.toEntity() })
        }
    }
}
