package com.example.planner.data.local.dao

import androidx.room.*
import com.example.planner.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'ADMIN' LIMIT 1")
    suspend fun getAdmin(): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("UPDATE users SET points = points + :points WHERE id = :userId")
    suspend fun addPoints(userId: Long, points: Int)

    @Query("UPDATE users SET tasksCompleted = tasksCompleted + 1 WHERE id = :userId")
    suspend fun incrementTasksCompleted(userId: Long)

    @Query("UPDATE users SET currentStreak = :streak, longestStreak = CASE WHEN :streak > longestStreak THEN :streak ELSE longestStreak END, lastTaskCompletionDate = :date WHERE id = :userId")
    suspend fun updateStreak(userId: Long, streak: Int, date: Long)

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, timestamp: Long)

    @Query("SELECT * FROM users ORDER BY points DESC")
    fun getLeaderboard(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY points DESC LIMIT :limit")
    fun getTopUsers(limit: Int): Flow<List<UserEntity>>
}
