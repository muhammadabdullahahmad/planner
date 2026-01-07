package com.example.planner.data.repository

import com.example.planner.data.local.dao.EventDao
import com.example.planner.domain.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {

    override suspend fun createEvent(event: Event): Long {
        return eventDao.insert(event.toEntity())
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.update(event.toEntity())
    }

    override suspend fun deleteEvent(eventId: Long) {
        eventDao.deleteById(eventId)
    }

    override suspend fun getEventById(id: Long): Event? {
        return eventDao.getEventById(id)?.let { Event.fromEntity(it) }
    }

    override fun getEventByIdFlow(id: Long): Flow<Event?> {
        return eventDao.getEventByIdFlow(id).map { entity ->
            entity?.let { Event.fromEntity(it) }
        }
    }

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }

    override fun getEventsForDate(startOfDay: Long, endOfDay: Long): Flow<List<Event>> {
        return eventDao.getEventsForDate(startOfDay, endOfDay).map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }

    override fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Event>> {
        return eventDao.getEventsForMonth(startOfMonth, endOfMonth).map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }

    override fun getEventsByType(type: String): Flow<List<Event>> {
        return eventDao.getEventsByType(type).map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }

    override fun getEventsCreatedByUser(userId: Long): Flow<List<Event>> {
        return eventDao.getEventsCreatedByUser(userId).map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }

    override fun getUpcomingEvents(limit: Int): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(System.currentTimeMillis(), limit).map { entities ->
            entities.map { Event.fromEntity(it) }
        }
    }
}
