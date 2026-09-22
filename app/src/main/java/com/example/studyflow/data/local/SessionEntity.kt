package com.example.studyflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: Long,
    val email: String,
    val loggedInAt: Long = System.currentTimeMillis()
)