package com.example.planner.data.local.dao

import androidx.room.*
import com.example.planner.data.local.entity.TaskCommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: TaskCommentEntity): Long

    @Update
    suspend fun update(comment: TaskCommentEntity)

    @Delete
    suspend fun delete(comment: TaskCommentEntity)

    @Query("DELETE FROM task_comments WHERE id = :commentId")
    suspend fun deleteById(commentId: Long)

    @Query("SELECT * FROM task_comments WHERE id = :id")
    suspend fun getCommentById(id: Long): TaskCommentEntity?

    @Query("SELECT * FROM task_comments WHERE taskId = :taskId ORDER BY createdAt ASC")
    fun getCommentsForTask(taskId: Long): Flow<List<TaskCommentEntity>>

    @Query("SELECT COUNT(*) FROM task_comments WHERE taskId = :taskId")
    suspend fun getCommentCountForTask(taskId: Long): Int
}
