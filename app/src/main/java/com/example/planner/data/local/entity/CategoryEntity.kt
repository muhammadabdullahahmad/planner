package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String, // Material icon name
    val colorHex: String, // Hex color code
    val isDefault: Boolean = false, // Pre-defined categories
    val createdAt: Long = System.currentTimeMillis()
)
