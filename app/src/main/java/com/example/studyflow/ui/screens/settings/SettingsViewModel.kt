package com.example.studyflow.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = (app as StudyFlowApp).database
    private val session = (app as StudyFlowApp).sessionManager
    private val userDao: UserDao = db.userDao()

    fun updateProfile(
        displayName: String?,
        preferredLanguage: String,
        dailyGoalMinutes: Int,
        theme: String,
        onDone: () -> Unit
    ) {
        val uid = session.currentUserId() ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = userDao.findById(uid) ?: return@withContext
                userDao.insert(
                    user.copy(
                        displayName = displayName?.trim()?.ifBlank { null },
                        preferredLanguage = preferredLanguage,
                        dailyStudyGoalMinutes = dailyGoalMinutes,
                        theme = theme
                    )
                )
            }
            onDone()
        }
    }
}