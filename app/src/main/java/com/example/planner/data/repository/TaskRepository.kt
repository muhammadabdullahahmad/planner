package com.example.planner.data.repository

import com.example.planner.data.local.entity.TaskAssigneeEntity
import com.example.planner.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task, assigneeIds: List<Long>): Long
    suspend fun updateTask(task: Task, assigneeIds: List<Long>? = null)
    suspend fun deleteTask(taskId: Long)
    suspend fun getTaskById(id: Long): Task?
    fun getTaskByIdFlow(id: Long): Flow<Task?>
    fun getAllTasks(): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>
    fun getTasksByPriority(priority: String): Flow<List<Task>>
    suspend fun updateTaskStatus(taskId: Long, status: String)
    fun getTasksForUser(userId: Long): Flow<List<Task>>
    fun getActiveTasksForUser(userId: Long): Flow<List<Task>>
    fun getCompletedTasksForUser(userId: Long): Flow<List<Task>>
    fun getTasksForUserOnDate(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<Task>>
    suspend fun markCompletedByUser(taskId: Long, userId: Long)
    suspend fun getCompletedTaskCountForUser(userId: Long): Int
    fun getAssigneesForTask(taskId: Long): Flow<List<TaskAssigneeEntity>>
    suspend fun startTask(taskId: Long)
    suspend fun isCompletedByUser(taskId: Long, userId: Long): Boolean
}
