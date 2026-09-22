package com.example.studyflow.data.repository

import com.example.studyflow.data.local.SyncStatus
import com.example.studyflow.data.local.TaskDao
import com.example.studyflow.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun observeAll(userId: Long): Flow<List<TaskEntity>> = dao.observeAll(userId)

    fun observeUpcoming(userId: Long): Flow<List<TaskEntity>> = dao.observeUpcoming(userId)

    fun observeCompleted(userId: Long): Flow<List<TaskEntity>> = dao.observeCompleted(userId)

    fun observePendingCount(userId: Long): Flow<Int> = dao.observePendingCount(userId)

    fun observeCompletedCount(userId: Long): Flow<Int> = dao.observeCompletedCount(userId)

    suspend fun create(
        userId: Long,
        title: String,
        description: String?,
        subject: String?,
        dueDate: Long?,
        priority: com.example.studyflow.data.local.TaskPriority
    ): Long {
        val task = TaskEntity(
            userId = userId,
            title = title.trim(),
            description = description?.trim()?.ifBlank { null },
            subject = subject?.trim()?.ifBlank { null },
            dueDate = dueDate,
            priority = priority,
            syncStatus = SyncStatus.PENDING
        )
        return dao.insert(task)
    }

    suspend fun update(task: TaskEntity) {
        dao.update(task.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncStatus.PENDING))
    }

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun setCompleted(id: Long, completed: Boolean) =
        dao.setCompleted(id, completed)

    suspend fun findById(id: Long): TaskEntity? = dao.findById(id)
}