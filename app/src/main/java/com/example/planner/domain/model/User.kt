package com.example.planner.domain.model

import com.example.planner.data.local.entity.UserEntity

data class User(
    val id: Long = 0,
    val name: String,
    val profilePictureUri: String? = null,
    val role: UserRole,
    val status: String = "Active",
    val points: Int = 0,
    val tasksCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastTaskCompletionDate: Long? = null,
    val colorHex: String = "#4CAF50",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
) {
    val isAdmin: Boolean get() = role == UserRole.ADMIN

    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User(
                id = entity.id,
                name = entity.name,
                profilePictureUri = entity.profilePictureUri,
                role = UserRole.valueOf(entity.role),
                status = entity.status,
                points = entity.points,
                tasksCompleted = entity.tasksCompleted,
                currentStreak = entity.currentStreak,
                longestStreak = entity.longestStreak,
                lastTaskCompletionDate = entity.lastTaskCompletionDate,
                colorHex = entity.colorHex,
                createdAt = entity.createdAt,
                lastLoginAt = entity.lastLoginAt
            )
        }
    }

    fun toEntity(pinHash: String): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            profilePictureUri = profilePictureUri,
            role = role.name,
            pinHash = pinHash,
            status = status,
            points = points,
            tasksCompleted = tasksCompleted,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastTaskCompletionDate = lastTaskCompletionDate,
            colorHex = colorHex,
            createdAt = createdAt,
            lastLoginAt = lastLoginAt
        )
    }
}
