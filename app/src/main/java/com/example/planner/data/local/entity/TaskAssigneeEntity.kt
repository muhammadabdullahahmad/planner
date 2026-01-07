package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "task_assignees",
    primaryKeys = ["taskId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class TaskAssigneeEntity(
    val taskId: Long,
    val userId: Long,
    val assignedAt: Long = System.currentTimeMillis(),
    val completedByUser: Boolean = false,
    val completedAt: Long? = null
)
