package com.example.planner.data.repository

import com.example.planner.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun createEvent(event: Event): Long
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(eventId: Long)
    suspend fun getEventById(id: Long): Event?
    fun getEventByIdFlow(id: Long): Flow<Event?>
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsForDate(startOfDay: Long, endOfDay: Long): Flow<List<Event>>
    fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Event>>
    fun getEventsByType(type: String): Flow<List<Event>>
    fun getEventsCreatedByUser(userId: Long): Flow<List<Event>>
    fun getUpcomingEvents(limit: Int): Flow<List<Event>>
}
