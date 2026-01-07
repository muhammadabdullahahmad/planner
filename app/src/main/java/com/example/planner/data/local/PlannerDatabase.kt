package com.example.planner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.planner.data.local.dao.*
import com.example.planner.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        TaskAssigneeEntity::class,
        CategoryEntity::class,
        EventEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        TaskCommentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PlannerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun eventDao(): EventDao
    abstract fun achievementDao(): AchievementDao
    abstract fun taskCommentDao(): TaskCommentDao

    companion object {
        const val DATABASE_NAME = "planner_database"
    }
}
