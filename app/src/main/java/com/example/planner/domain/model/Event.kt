package com.example.planner.domain.model

import com.example.planner.data.local.entity.EventEntity

data class Event(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val createdByUserId: Long?,
    val eventType: EventType,
    val startDate: Long,
    val endDate: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val isAllDay: Boolean = false,
    val colorHex: String,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null,
    val reminderMinutes: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromEntity(entity: EventEntity): Event {
            return Event(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                createdByUserId = entity.createdByUserId,
                eventType = EventType.valueOf(entity.eventType),
                startDate = entity.startDate,
                endDate = entity.endDate,
                startTime = entity.startTime,
                endTime = entity.endTime,
                isAllDay = entity.isAllDay,
                colorHex = entity.colorHex,
                isRecurring = entity.isRecurring,
                recurrenceType = entity.recurrenceType?.let { RecurrenceType.valueOf(it) },
                reminderMinutes = entity.reminderMinutes,
                createdAt = entity.createdAt
            )
        }
    }

    fun toEntity(): EventEntity {
        return EventEntity(
            id = id,
            title = title,
            description = description,
            createdByUserId = createdByUserId,
            eventType = eventType.name,
            startDate = startDate,
            endDate = endDate,
            startTime = startTime,
            endTime = endTime,
            isAllDay = isAllDay,
            colorHex = colorHex,
            isRecurring = isRecurring,
            recurrenceType = recurrenceType?.name,
            reminderMinutes = reminderMinutes,
            createdAt = createdAt
        )
    }
}
