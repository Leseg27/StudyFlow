package com.example.studyflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun observeAll(userId: Long): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    fun observeRecent(userId: Long, limit: Int): Flow<List<StudySessionEntity>>

    @Query("SELECT IFNULL(SUM(durationMinutes), 0) FROM study_sessions WHERE userId = :userId AND startedAt >= :since")
    fun observeTotalMinutesSince(userId: Long, since: Long): Flow<Int>

    @Insert
    suspend fun insert(session: StudySessionEntity): Long

    @Delete
    suspend fun delete(session: StudySessionEntity)
}