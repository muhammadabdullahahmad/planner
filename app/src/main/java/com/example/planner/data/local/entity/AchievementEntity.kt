package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val badgeColorHex: String,
    val requiredPoints: Int? = null,
    val requiredTaskCount: Int? = null,
    val requiredStreak: Int? = null,
    val achievementType: String // "POINTS", "TASKS", "STREAK", "CATEGORY", "SPECIAL"
)
