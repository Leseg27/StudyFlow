package com.example.studyflow.data.repository

import com.example.studyflow.data.local.SubjectDao
import com.example.studyflow.data.local.SubjectEntity
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val dao: SubjectDao) {

    fun observeAll(userId: Long): Flow<List<SubjectEntity>> = dao.observeAll(userId)

    suspend fun create(
        userId: Long,
        name: String,
        code: String?,
        colourHex: String,
        lecturer: String?
    ): Long = dao.insert(
        SubjectEntity(
            userId = userId,
            name = name.trim(),
            code = code?.trim()?.ifBlank { null },
            colourHex = colourHex,
            lecturer = lecturer?.trim()?.ifBlank { null }
        )
    )

    suspend fun update(subject: SubjectEntity) = dao.update(subject)

    suspend fun delete(subject: SubjectEntity) = dao.delete(subject)

    suspend fun findById(id: Long) = dao.findById(id)
}