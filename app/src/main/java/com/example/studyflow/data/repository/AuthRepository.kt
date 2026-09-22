package com.example.studyflow.data.repository

import android.content.Context
import com.example.studyflow.data.local.SessionEntity
import com.example.studyflow.data.local.UserEntity
import com.example.studyflow.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val db: AppDatabase) {

    data class Result(
        val success: Boolean,
        val message: String? = null,
        val userId: Long? = null
    )

    suspend fun register(
        email: String,
        password: String,
        displayName: String? = null
    ): Result = withContext(Dispatchers.IO) {
        val normalized = email.trim().lowercase()

        if (db.userDao().findByEmail(normalized) != null) {
            return@withContext Result(false, "An account with this email already exists")
        }

        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash(password, salt)

        val id = db.userDao().insert(
            UserEntity(
                email = normalized,
                displayName = displayName,
                passwordHash = hash,
                salt = salt
            )
        )
        Result(true, userId = id)
    }

    suspend fun login(email: String, password: String): Result =
        withContext(Dispatchers.IO) {
            val normalized = email.trim().lowercase()
            val user = db.userDao().findByEmail(normalized)
                ?: return@withContext Result(false, "No account found for this email")

            val valid = PasswordHasher.verify(password, user.salt, user.passwordHash)
            if (!valid) return@withContext Result(false, "Incorrect password")

            db.sessionDao().save(
                SessionEntity(userId = user.id, email = user.email)
            )
            Result(true, userId = user.id)
        }

    suspend fun logout() = withContext(Dispatchers.IO) {
        db.sessionDao().clear()
    }

    suspend fun currentSession(): SessionEntity? = withContext(Dispatchers.IO) {
        db.sessionDao().get()
    }
}