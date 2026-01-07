package com.example.planner.data.local.dao

import androidx.room.*
import com.example.planner.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventByIdFlow(id: Long): Flow<EventEntity?>

    @Query("SELECT * FROM events ORDER BY startDate ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startDate BETWEEN :startOfDay AND :endOfDay ORDER BY startTime ASC")
    fun getEventsForDate(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startDate BETWEEN :startOfMonth AND :endOfMonth ORDER BY startDate ASC, startTime ASC")
    fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE eventType = :type ORDER BY startDate ASC")
    fun getEventsByType(type: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE createdByUserId = :userId ORDER BY startDate ASC")
    fun getEventsCreatedByUser(userId: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startDate >= :today ORDER BY startDate ASC LIMIT :limit")
    fun getUpcomingEvents(today: Long, limit: Int): Flow<List<EventEntity>>
}
