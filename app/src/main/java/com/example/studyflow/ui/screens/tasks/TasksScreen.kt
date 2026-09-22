package com.example.studyflow.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyflow.data.local.TaskEntity
import com.example.studyflow.data.local.TaskPriority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onBack: () -> Unit,
    vm: TaskViewModel = viewModel()
) {
    val tasks by vm.tasks.collectAsState()
    val filter by vm.filter.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tasks",
                        color = Color(0xFFE8EEF6),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFE8EEF6)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F2744)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTask = null
                    showAddSheet = true
                },
                containerColor = Color(0xFF2E9B68),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0F2744), Color(0xFF173B67))
                    )
                )
                .padding(innerPadding)
        ) {
            Column(Modifier.fillMaxSize()) {

                // ---------- Filter chips ----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskFilter.values().forEach { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { vm.setFilter(f) },
                            label = {
                                Text(
                                    when (f) {
                                        TaskFilter.ALL -> "All"
                                        TaskFilter.UPCOMING -> "Upcoming"
                                        TaskFilter.COMPLETED -> "Done"
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E9B68),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF173B67),
                                labelColor = Color(0xFFE8EEF6)
                            )
                        )
                    }
                }

                // ---------- Task list or empty state ----------
                if (tasks.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            TaskRow(
                                task = task,
                                onToggle = { vm.toggleCompleted(task) },
                                onEdit = {
                                    editingTask = task
                                    showAddSheet = true
                                },
                                onDelete = { pendingDelete = task }
                            )
                        }
                    }
                }
            }
        }
    }

    // ---------- Add / Edit bottom sheet ----------
    if (showAddSheet) {
        AddEditTaskSheet(
            existing = editingTask,
            onDismiss = {
                showAddSheet = false
                editingTask = null
            },
            onSave = { title, desc, subject, due, priority ->
                if (editingTask == null) {
                    vm.createTask(title, desc, subject, due, priority)
                } else {
                    vm.updateTask(
                        editingTask!!.copy(
                            title = title,
                            description = desc,
                            subject = subject,
                            dueDate = due,
                            priority = priority
                        )
                    )
                }
                showAddSheet = false
                editingTask = null
            }
        )
    }

    // ---------- Delete confirmation ----------
    pendingDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete task?") },
            text = { Text("\"${task.title}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteTask(task)
                    pendingDelete = null
                }) {
                    Text("Delete", color = Color(0xFFD64545))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------------------------------------------------------
// Small composables
// ----------------------------------------------------------------

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "No tasks yet",
                color = Color(0xFFE8EEF6),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Tap the + button to add your first task",
                color = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF173B67), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2E9B68),
                uncheckedColor = Color(0xFFE8EEF6).copy(alpha = 0.6f),
                checkmarkColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            Text(
                task.title,
                color = Color(0xFFE8EEF6),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )

            val meta = buildMeta(task)
            if (meta.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    meta,
                    color = Color(0xFFE8EEF6).copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        PriorityBadge(task.priority)

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: TaskPriority) {
    val (label, color) = when (priority) {
        TaskPriority.LOW -> "Low" to Color(0xFF4F8A5B)
        TaskPriority.MEDIUM -> "Med" to Color(0xFFC78A2E)
        TaskPriority.HIGH -> "High" to Color(0xFFD64545)
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ----------------------------------------------------------------
// Helpers
// ----------------------------------------------------------------

private fun buildMeta(task: TaskEntity): String {
    val parts = mutableListOf<String>()
    task.subject?.let { parts += it }
    task.dueDate?.let { parts += "Due ${formatDate(it)}" }
    return parts.joinToString(" • ")
}

private fun formatDate(epochMillis: Long): String {
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return fmt.format(Date(epochMillis))
}