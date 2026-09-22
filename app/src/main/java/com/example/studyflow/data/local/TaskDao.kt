package com.example.studyflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /** All tasks for a user, most relevant first. */
    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId
        ORDER BY
            isCompleted ASC,
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC,
            priority DESC,
            createdAt DESC
        """
    )
    fun observeAll(userId: Long): Flow<List<TaskEntity>>

    /** Only upcoming (not completed). */
    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId AND isCompleted = 0
        ORDER BY dueDate ASC
        """
    )
    fun observeUpcoming(userId: Long): Flow<List<TaskEntity>>

    /** Only completed. */
    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId AND isCompleted = 1
        ORDER BY completedAt DESC
        """
    )
    fun observeCompleted(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        UPDATE tasks
        SET isCompleted = :completed,
            completedAt = CASE WHEN :completed = 1 THEN :now ELSE NULL END,
            updatedAt = :now,
            syncStatus = 'PENDING'
        WHERE id = :id
        """
    )
    suspend fun setCompleted(id: Long, completed: Boolean, now: Long = System.currentTimeMillis())

    /** Counts for dashboard / stats. */
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0")
    fun observePendingCount(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    fun observeCompletedCount(userId: Long): Flow<Int>
}