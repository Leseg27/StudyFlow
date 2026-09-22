package com.example.studyflow.data.repository

import com.example.studyflow.data.local.StudySessionDao
import com.example.studyflow.data.local.StudySessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudySessionRepository(private val dao: StudySessionDao) {

    fun observeAll(userId: Long): Flow<List<StudySessionEntity>> = dao.observeAll(userId)

    fun observeRecent(userId: Long, limit: Int = 5) = dao.observeRecent(userId, limit)

    fun observeWeekMinutes(userId: Long): Flow<Int> =
        dao.observeTotalMinutesSince(userId, startOfWeek())

    fun observeTodayMinutes(userId: Long): Flow<Int> =
        dao.observeTotalMinutesSince(userId, startOfToday())

    suspend fun add(
        userId: Long,
        subjectId: Long?,
        durationMinutes: Int,
        notes: String?
    ): Long = dao.insert(
        StudySessionEntity(
            userId = userId,
            subjectId = subjectId,
            startedAt = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            notes = notes?.trim()?.ifBlank { null }
        )
    )

    suspend fun delete(session: StudySessionEntity) = dao.delete(session)

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun startOfWeek(): Long = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}