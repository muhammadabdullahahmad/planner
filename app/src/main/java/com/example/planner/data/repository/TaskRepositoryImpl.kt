package com.example.planner.data.repository

import com.example.planner.data.local.dao.TaskDao
import com.example.planner.data.local.entity.TaskAssigneeEntity
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun createTask(task: Task, assigneeIds: List<Long>): Long {
        val taskId = taskDao.insert(task.toEntity())
        val assignees = assigneeIds.map { userId ->
            TaskAssigneeEntity(taskId = taskId, userId = userId)
        }
        taskDao.insertAssignees(assignees)
        return taskId
    }

    override suspend fun updateTask(task: Task, assigneeIds: List<Long>?) {
        taskDao.update(task.toEntity())
        assigneeIds?.let { ids ->
            taskDao.deleteAllAssigneesForTask(task.id)
            val assignees = ids.map { userId ->
                TaskAssigneeEntity(taskId = task.id, userId = userId)
            }
            taskDao.insertAssignees(assignees)
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteById(taskId)
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.let { Task.fromEntity(it) }
    }

    override fun getTaskByIdFlow(id: Long): Flow<Task?> {
        return taskDao.getTaskByIdFlow(id).map { entity ->
            entity?.let { Task.fromEntity(it) }
        }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>> {
        return taskDao.getTasksForDate(startOfDay, endOfDay).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getTasksByCategory(categoryId: Long): Flow<List<Task>> {
        return taskDao.getTasksByCategory(categoryId).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getTasksByPriority(priority: String): Flow<List<Task>> {
        return taskDao.getTasksByPriority(priority).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override suspend fun updateTaskStatus(taskId: Long, status: String) {
        taskDao.updateStatus(taskId, status)
    }

    override fun getTasksForUser(userId: Long): Flow<List<Task>> {
        return taskDao.getTasksForUser(userId).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getActiveTasksForUser(userId: Long): Flow<List<Task>> {
        return taskDao.getActiveTasksForUser(userId).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getCompletedTasksForUser(userId: Long): Flow<List<Task>> {
        return taskDao.getCompletedTasksForUser(userId).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override fun getTasksForUserOnDate(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<Task>> {
        return taskDao.getTasksForUserOnDate(userId, startOfDay, endOfDay).map { entities ->
            entities.map { Task.fromEntity(it) }
        }
    }

    override suspend fun markCompletedByUser(taskId: Long, userId: Long) {
        taskDao.markCompletedByUser(taskId, userId)
        // Check if all assignees have completed
        val pendingCount = taskDao.getPendingAssigneeCount(taskId)
        if (pendingCount == 0) {
            taskDao.updateStatus(taskId, TaskStatus.COMPLETED.name)
        }
    }

    override suspend fun getCompletedTaskCountForUser(userId: Long): Int {
        return taskDao.getCompletedTaskCountForUser(userId)
    }

    override fun getAssigneesForTask(taskId: Long): Flow<List<TaskAssigneeEntity>> {
        return taskDao.getAssigneesForTask(taskId)
    }

    override suspend fun startTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId)
        if (task?.status == TaskStatus.PENDING.name) {
            taskDao.updateStatus(taskId, TaskStatus.IN_PROGRESS.name)
        }
    }

    override suspend fun isCompletedByUser(taskId: Long, userId: Long): Boolean {
        return taskDao.isCompletedByUser(taskId, userId) ?: false
    }
}
