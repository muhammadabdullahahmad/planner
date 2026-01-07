package com.example.planner.data.repository

import com.example.planner.data.local.dao.UserDao
import com.example.planner.domain.model.User
import com.example.planner.domain.model.UserRole
import com.example.planner.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun createUser(user: User, pin: String): Long {
        val pinHash = PasswordHasher.hash(pin)
        return userDao.insert(user.toEntity(pinHash))
    }

    override suspend fun updateUser(user: User, pin: String?) {
        val existingUser = userDao.getUserById(user.id)
        existingUser?.let {
            val pinHash = pin?.let { p -> PasswordHasher.hash(p) } ?: it.pinHash
            userDao.update(user.toEntity(pinHash))
        }
    }

    override suspend fun deleteUser(user: User) {
        val existingUser = userDao.getUserById(user.id)
        existingUser?.let { userDao.delete(it) }
    }

    override suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)?.let { User.fromEntity(it) }
    }

    override fun getUserByIdFlow(id: Long): Flow<User?> {
        return userDao.getUserByIdFlow(id).map { entity ->
            entity?.let { User.fromEntity(it) }
        }
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { User.fromEntity(it) }
        }
    }

    override fun getUsersByRole(role: String): Flow<List<User>> {
        return userDao.getUsersByRole(role).map { entities ->
            entities.map { User.fromEntity(it) }
        }
    }

    override suspend fun getAdmin(): User? {
        return userDao.getAdmin()?.let { User.fromEntity(it) }
    }

    override suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    override suspend fun verifyPin(userId: Long, pin: String): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        return PasswordHasher.verify(pin, user.pinHash)
    }

    override suspend fun addPoints(userId: Long, points: Int) {
        userDao.addPoints(userId, points)
    }

    override suspend fun incrementTasksCompleted(userId: Long) {
        userDao.incrementTasksCompleted(userId)
    }

    override suspend fun updateStreak(userId: Long, streak: Int, date: Long) {
        userDao.updateStreak(userId, streak, date)
    }

    override suspend fun updateLastLogin(userId: Long, timestamp: Long) {
        userDao.updateLastLogin(userId, timestamp)
    }

    override fun getLeaderboard(): Flow<List<User>> {
        return userDao.getLeaderboard().map { entities ->
            entities.map { User.fromEntity(it) }
        }
    }

    override fun getTopUsers(limit: Int): Flow<List<User>> {
        return userDao.getTopUsers(limit).map { entities ->
            entities.map { User.fromEntity(it) }
        }
    }
}
