package com.example.planner.data.local.dao

import androidx.room.*
import com.example.planner.data.local.entity.AchievementEntity
import com.example.planner.data.local.entity.UserAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: AchievementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Delete
    suspend fun delete(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: Long): AchievementEntity?

    @Query("SELECT * FROM achievements ORDER BY name ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE achievementType = :type")
    fun getAchievementsByType(type: String): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementCount(): Int

    // User Achievements
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun awardAchievement(userAchievement: UserAchievementEntity)

    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getUserAchievements(userId: Long): Flow<List<UserAchievementEntity>>

    @Query("SELECT a.* FROM achievements a INNER JOIN user_achievements ua ON a.id = ua.achievementId WHERE ua.userId = :userId ORDER BY ua.earnedAt DESC")
    fun getEarnedAchievementsForUser(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT a.* FROM achievements a WHERE a.id NOT IN (SELECT achievementId FROM user_achievements WHERE userId = :userId)")
    fun getUnearnedAchievementsForUser(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM user_achievements WHERE userId = :userId")
    suspend fun getEarnedAchievementCountForUser(userId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM user_achievements WHERE userId = :userId AND achievementId = :achievementId)")
    suspend fun hasUserEarnedAchievement(userId: Long, achievementId: Long): Boolean

    @Query("SELECT * FROM user_achievements WHERE userId = :userId ORDER BY earnedAt DESC LIMIT 1")
    suspend fun getLatestAchievementForUser(userId: Long): UserAchievementEntity?
}
