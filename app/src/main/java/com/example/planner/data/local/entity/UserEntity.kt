package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val profilePictureUri: String? = null,
    val role: String, // "ADMIN" or "MEMBER"
    val pinHash: String,
    val status: String = "Active", // Active, Away, Busy
    val points: Int = 0,
    val tasksCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastTaskCompletionDate: Long? = null,
    val colorHex: String = "#4CAF50", // Member color for calendar
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)
