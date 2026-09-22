package com.example.studyflow.ui.screens.study

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.data.local.StudySessionEntity
import com.example.studyflow.data.local.SubjectEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudySessionViewModel(app: Application) : AndroidViewModel(app) {

    private val sessionRepo = (app as StudyFlowApp).studySessionRepository
    private val subjectRepo = (app as StudyFlowApp).subjectRepository
    private val session = (app as StudyFlowApp).sessionManager

    val recent: StateFlow<List<StudySessionEntity>> =
        sessionRepo.observeRecent(session.currentUserId() ?: -1L, 10).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
        )

    val todayMinutes: StateFlow<Int> =
        sessionRepo.observeTodayMinutes(session.currentUserId() ?: -1L).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), 0
        )

    val weekMinutes: StateFlow<Int> =
        sessionRepo.observeWeekMinutes(session.currentUserId() ?: -1L).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), 0
        )

    val subjects: StateFlow<List<SubjectEntity>> =
        subjectRepo.observeAll(session.currentUserId() ?: -1L).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
        )

    fun addSession(subjectId: Long?, minutes: Int, notes: String?) {
        val uid = session.currentUserId() ?: return
        viewModelScope.launch { sessionRepo.add(uid, subjectId, minutes, notes) }
    }

    fun delete(s: StudySessionEntity) {
        viewModelScope.launch { sessionRepo.delete(s) }
    }
}