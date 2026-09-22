package com.example.studyflow

import android.app.Application
import com.example.studyflow.data.local.AppDatabase
import com.example.studyflow.data.prefs.SessionManager
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.StudySessionRepository
import com.example.studyflow.data.repository.SubjectRepository
import com.example.studyflow.data.repository.TaskRepository

class StudyFlowApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val sessionManager: SessionManager by lazy { SessionManager(this) }

    val authRepository: AuthRepository by lazy { AuthRepository(database) }
    val taskRepository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
    val subjectRepository: SubjectRepository by lazy { SubjectRepository(database.subjectDao()) }
    val studySessionRepository: StudySessionRepository by lazy {
        StudySessionRepository(database.studySessionDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: StudyFlowApp
            private set
    }
}