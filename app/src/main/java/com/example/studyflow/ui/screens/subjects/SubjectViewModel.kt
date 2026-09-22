package com.example.studyflow.ui.screens.subjects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.data.local.SubjectEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubjectViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as StudyFlowApp).subjectRepository
    private val session = (app as StudyFlowApp).sessionManager

    val subjects: StateFlow<List<SubjectEntity>> =
        repo.observeAll(session.currentUserId() ?: -1L).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun create(name: String, code: String?, colourHex: String, lecturer: String?) {
        val uid = session.currentUserId() ?: return
        viewModelScope.launch { repo.create(uid, name, code, colourHex, lecturer) }
    }

    fun update(subject: SubjectEntity) {
        viewModelScope.launch { repo.update(subject) }
    }

    fun delete(subject: SubjectEntity) {
        viewModelScope.launch { repo.delete(subject) }
    }
}