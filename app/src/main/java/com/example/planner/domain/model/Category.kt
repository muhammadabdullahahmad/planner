package com.example.planner.domain.model

import com.example.planner.data.local.entity.CategoryEntity

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromEntity(entity: CategoryEntity): Category {
            return Category(
                id = entity.id,
                name = entity.name,
                iconName = entity.iconName,
                colorHex = entity.colorHex,
                isDefault = entity.isDefault,
                createdAt = entity.createdAt
            )
        }

        fun getDefaultCategories(): List<Category> {
            return TaskCategory.entries.map { taskCategory ->
                Category(
                    name = taskCategory.displayName,
                    iconName = taskCategory.name.lowercase(),
                    colorHex = taskCategory.colorHex,
                    isDefault = true
                )
            }
        }
    }

    fun toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isDefault = isDefault,
            createdAt = createdAt
        )
    }
}
