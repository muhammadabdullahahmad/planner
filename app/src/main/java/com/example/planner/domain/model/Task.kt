package com.example.planner.domain.model

import com.example.planner.data.local.entity.TaskEntity

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val createdByUserId: Long?,
    val categoryId: Long?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: Long?,
    val dueTime: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null,
    val recurrenceEndDate: Long? = null,
    val pointsValue: Int = 10,
    val assignedToAll: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val assignees: List<User> = emptyList(),
    val category: Category? = null
) {
    val isCompleted: Boolean get() = status == TaskStatus.COMPLETED
    val isOverdue: Boolean get() = !isCompleted && dueDate != null && dueDate < System.currentTimeMillis()

    companion object {
        fun fromEntity(entity: TaskEntity): Task {
            return Task(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                createdByUserId = entity.createdByUserId,
                categoryId = entity.categoryId,
                priority = TaskPriority.valueOf(entity.priority),
                status = TaskStatus.valueOf(entity.status),
                dueDate = entity.dueDate,
                dueTime = entity.dueTime,
                isRecurring = entity.isRecurring,
                recurrenceType = entity.recurrenceType?.let { RecurrenceType.valueOf(it) },
                recurrenceEndDate = entity.recurrenceEndDate,
                pointsValue = entity.pointsValue,
                assignedToAll = entity.assignedToAll,
                createdAt = entity.createdAt,
                completedAt = entity.completedAt
            )
        }
    }

    fun toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = description,
            createdByUserId = createdByUserId,
            categoryId = categoryId,
            priority = priority.name,
            status = status.name,
            dueDate = dueDate,
            dueTime = dueTime,
            isRecurring = isRecurring,
            recurrenceType = recurrenceType?.name,
            recurrenceEndDate = recurrenceEndDate,
            pointsValue = pointsValue,
            assignedToAll = assignedToAll,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }
}
