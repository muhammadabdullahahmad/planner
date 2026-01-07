package com.example.planner.data.repository

import com.example.planner.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    suspend fun initializeDefaultAchievements()
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getEarnedAchievementsForUser(userId: Long): Flow<List<Achievement>>
    fun getUnearnedAchievementsForUser(userId: Long): Flow<List<Achievement>>
    suspend fun awardAchievement(userId: Long, achievementId: Long)
    suspend fun hasUserEarnedAchievement(userId: Long, achievementId: Long): Boolean
    suspend fun checkAndAwardAchievements(userId: Long, points: Int, tasksCompleted: Int, streak: Int)
}
