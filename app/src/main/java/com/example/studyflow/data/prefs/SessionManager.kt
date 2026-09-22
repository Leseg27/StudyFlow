package com.example.studyflow.data.prefs

import android.content.Context
import androidx.core.content.edit
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.data.local.SessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("studyflow_session", Context.MODE_PRIVATE)
    private val dao = (context.applicationContext as StudyFlowApp).database.sessionDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    /** Fast synchronous check for the splash screen. */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun currentEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun markLoggedIn(userId: Long, email: String) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
        }
    }
    fun currentUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1L)
        return if (id > 0) id else null
    }

    // (existing) markLoggedIn writes KEY_USER_ID already — no change needed
    /** Persist the Room session row (called from AuthRepository.login). */
    fun mirrorToRoom(session: SessionEntity) {
        scope.launch { dao.save(session) }
    }

    fun clear() {
        prefs.edit { clear() }
        scope.launch { dao.clear() }
    }

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
    }
}