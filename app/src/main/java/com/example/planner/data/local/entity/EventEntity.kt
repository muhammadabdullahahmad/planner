package com.example.planner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("createdByUserId"), Index("startDate")]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val createdByUserId: Long?,
    val eventType: String, // "BIRTHDAY", "APPOINTMENT", "FAMILY_EVENT", "SCHEDULE"
    val startDate: Long,
    val endDate: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val isAllDay: Boolean = false,
    val colorHex: String, // For member color-coding
    val isRecurring: Boolean = false,
    val recurrenceType: String? = null,
    val reminderMinutes: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
