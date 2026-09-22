package com.example.studyflow.ui.screens.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.data.local.TaskEntity
import com.example.studyflow.data.local.TaskPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, UPCOMING, COMPLETED }

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as StudyFlowApp).taskRepository
    private val session = (app as StudyFlowApp).sessionManager

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    val tasks: StateFlow<List<TaskEntity>> =
        _filter.flatMapLatest { f ->
            val uid = session.currentUserId() ?: -1L
            when (f) {
                TaskFilter.ALL -> repo.observeAll(uid)
                TaskFilter.UPCOMING -> repo.observeUpcoming(uid)
                TaskFilter.COMPLETED -> repo.observeCompleted(uid)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val pendingCount: StateFlow<Int> =
        session.currentUserId()?.let { uid ->
            repo.observePendingCount(uid)
        }?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
            ?: MutableStateFlow(0).asStateFlow()

    val completedCount: StateFlow<Int> =
        session.currentUserId()?.let { uid ->
            repo.observeCompletedCount(uid)
        }?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
            ?: MutableStateFlow(0).asStateFlow()

    fun setFilter(f: TaskFilter) { _filter.value = f }

    fun createTask(
        title: String,
        description: String?,
        subject: String?,
        dueDate: Long?,
        priority: TaskPriority
    ) {
        val uid = session.currentUserId() ?: return
        viewModelScope.launch {
            repo.create(uid, title, description, subject, dueDate, priority)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch { repo.update(task) }
    }

    fun toggleCompleted(task: TaskEntity) {
        viewModelScope.launch { repo.setCompleted(task.id, !task.isCompleted) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repo.delete(task) }
    }
}