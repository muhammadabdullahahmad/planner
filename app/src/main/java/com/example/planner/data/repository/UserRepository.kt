package com.example.planner.data.repository

import com.example.planner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: User, pin: String): Long
    suspend fun updateUser(user: User, pin: String? = null)
    suspend fun deleteUser(user: User)
    suspend fun getUserById(id: Long): User?
    fun getUserByIdFlow(id: Long): Flow<User?>
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(role: String): Flow<List<User>>
    suspend fun getAdmin(): User?
    suspend fun getUserCount(): Int
    suspend fun verifyPin(userId: Long, pin: String): Boolean
    suspend fun addPoints(userId: Long, points: Int)
    suspend fun incrementTasksCompleted(userId: Long)
    suspend fun updateStreak(userId: Long, streak: Int, date: Long)
    suspend fun updateLastLogin(userId: Long, timestamp: Long)
    fun getLeaderboard(): Flow<List<User>>
    fun getTopUsers(limit: Int): Flow<List<User>>
}
