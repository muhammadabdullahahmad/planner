package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("createdByUserId"),
        Index("categoryId"),
        Index("dueDate"),
        Index("status")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val createdByUserId: Long?,
    val categoryId: Long?,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val status: String, // "PENDING", "IN_PROGRESS", "COMPLETED"
    val dueDate: Long?, // Timestamp
    val dueTime: String? = null, // HH:mm format
    val isRecurring: Boolean = false,
    val recurrenceType: String? = null, // "DAILY", "WEEKLY", "MONTHLY"
    val recurrenceEndDate: Long? = null,
    val pointsValue: Int = 10,
    val assignedToAll: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
