package com.example.planner.data.local.dao

import androidx.room.*
import com.example.planner.data.local.entity.TaskAssigneeEntity
import com.example.planner.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' ORDER BY dueDate ASC, priority DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay ORDER BY dueTime ASC, priority DESC")
    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY dueDate ASC")
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET status = :status, completedAt = CASE WHEN :status = 'COMPLETED' THEN :timestamp ELSE NULL END WHERE id = :taskId")
    suspend fun updateStatus(taskId: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    suspend fun getCompletedTaskCount(): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'COMPLETED' AND dueDate < :now")
    suspend fun getOverdueTaskCount(now: Long): Int

    // Task Assignees
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignee(assignee: TaskAssigneeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignees(assignees: List<TaskAssigneeEntity>)

    @Delete
    suspend fun deleteAssignee(assignee: TaskAssigneeEntity)

    @Query("DELETE FROM task_assignees WHERE taskId = :taskId")
    suspend fun deleteAllAssigneesForTask(taskId: Long)

    @Query("SELECT * FROM task_assignees WHERE taskId = :taskId")
    fun getAssigneesForTask(taskId: Long): Flow<List<TaskAssigneeEntity>>

    @Query("SELECT t.* FROM tasks t INNER JOIN task_assignees ta ON t.id = ta.taskId WHERE ta.userId = :userId ORDER BY t.dueDate ASC, t.priority DESC")
    fun getTasksForUser(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT t.* FROM tasks t INNER JOIN task_assignees ta ON t.id = ta.taskId WHERE ta.userId = :userId AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC, t.priority DESC")
    fun getActiveTasksForUser(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT t.* FROM tasks t INNER JOIN task_assignees ta ON t.id = ta.taskId WHERE ta.userId = :userId AND t.status = 'COMPLETED' ORDER BY t.completedAt DESC")
    fun getCompletedTasksForUser(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT t.* FROM tasks t INNER JOIN task_assignees ta ON t.id = ta.taskId WHERE ta.userId = :userId AND t.dueDate BETWEEN :startOfDay AND :endOfDay ORDER BY t.dueTime ASC, t.priority DESC")
    fun getTasksForUserOnDate(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("UPDATE task_assignees SET completedByUser = 1, completedAt = :timestamp WHERE taskId = :taskId AND userId = :userId")
    suspend fun markCompletedByUser(taskId: Long, userId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM task_assignees WHERE taskId = :taskId AND completedByUser = 0")
    suspend fun getPendingAssigneeCount(taskId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks t INNER JOIN task_assignees ta ON t.id = ta.taskId WHERE ta.userId = :userId AND t.status = 'COMPLETED'")
    suspend fun getCompletedTaskCountForUser(userId: Long): Int
}
