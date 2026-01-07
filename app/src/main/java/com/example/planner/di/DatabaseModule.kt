package com.example.planner.di

import android.content.Context
import androidx.room.Room
import com.example.planner.data.local.PlannerDatabase
import com.example.planner.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlannerDatabase {
        return Room.databaseBuilder(
            context,
            PlannerDatabase::class.java,
            PlannerDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: PlannerDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: PlannerDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: PlannerDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: PlannerDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: PlannerDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun provideTaskCommentDao(database: PlannerDatabase): TaskCommentDao {
        return database.taskCommentDao()
    }
}
