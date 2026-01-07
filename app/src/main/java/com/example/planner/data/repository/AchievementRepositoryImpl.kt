package com.example.planner.data.repository

import com.example.planner.data.local.dao.AchievementDao
import com.example.planner.data.local.entity.UserAchievementEntity
import com.example.planner.domain.model.Achievement
import com.example.planner.domain.model.AchievementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao
) : AchievementRepository {

    override suspend fun initializeDefaultAchievements() {
        val count = achievementDao.getAchievementCount()
        if (count == 0) {
            val defaults = Achievement.getDefaultAchievements()
            achievementDao.insertAll(defaults.map { it.toEntity() })
        }
    }

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            entities.map { Achievement.fromEntity(it) }
        }
    }

    override fun getEarnedAchievementsForUser(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getEarnedAchievementsForUser(userId).map { entities ->
            entities.map { Achievement.fromEntity(it).copy(isEarned = true) }
        }
    }

    override fun getUnearnedAchievementsForUser(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getUnearnedAchievementsForUser(userId).map { entities ->
            entities.map { Achievement.fromEntity(it) }
        }
    }

    override suspend fun awardAchievement(userId: Long, achievementId: Long) {
        achievementDao.awardAchievement(
            UserAchievementEntity(userId = userId, achievementId = achievementId)
        )
    }

    override suspend fun hasUserEarnedAchievement(userId: Long, achievementId: Long): Boolean {
        return achievementDao.hasUserEarnedAchievement(userId, achievementId)
    }

    override suspend fun checkAndAwardAchievements(
        userId: Long,
        points: Int,
        tasksCompleted: Int,
        streak: Int
    ) {
        val allAchievements = achievementDao.getAllAchievements().first()

        for (achievement in allAchievements) {
            val alreadyEarned = achievementDao.hasUserEarnedAchievement(userId, achievement.id)
            if (alreadyEarned) continue

            val shouldAward = when (AchievementType.valueOf(achievement.achievementType)) {
                AchievementType.POINTS -> {
                    achievement.requiredPoints != null && points >= achievement.requiredPoints
                }
                AchievementType.TASKS -> {
                    achievement.requiredTaskCount != null && tasksCompleted >= achievement.requiredTaskCount
                }
                AchievementType.STREAK -> {
                    achievement.requiredStreak != null && streak >= achievement.requiredStreak
                }
                else -> false
            }

            if (shouldAward) {
                awardAchievement(userId, achievement.id)
            }
        }
    }
}
