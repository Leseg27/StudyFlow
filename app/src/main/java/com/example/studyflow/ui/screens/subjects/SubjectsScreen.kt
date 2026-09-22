package com.example.studyflow.ui.screens.subjects

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyflow.data.local.SubjectEntity
import com.example.studyflow.ui.components.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    onBack: () -> Unit,
    vm: SubjectViewModel = viewModel()
) {
    val subjects by vm.subjects.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SubjectEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Subjects", color = Color(0xFFE8EEF6), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFE8EEF6)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2744))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = null; showSheet = true },
                containerColor = Color(0xFF2E9B68),
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Add subject") }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0F2744), Color(0xFF173B67))))
                .padding(innerPadding)
        ) {
            if (subjects.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No subjects yet — tap + to add one",
                        color = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(subjects, key = { it.id }) { subject ->
                        SubjectRow(
                            subject = subject,
                            onClick = { editing = subject; showSheet = true },
                            onDelete = { pendingDelete = subject }
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        AddEditSubjectSheet(
            existing = editing,
            onDismiss = { showSheet = false; editing = null },
            onSave = { name, code, colour, lecturer ->
                if (editing == null) {
                    vm.create(name, code, colour, lecturer)
                } else {
                    vm.update(
                        editing!!.copy(
                            name = name,
                            code = code,
                            colourHex = colour,
                            lecturer = lecturer
                        )
                    )
                }
                showSheet = false
                editing = null
            }
        )
    }

    pendingDelete?.let { s ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete subject?") },
            text = { Text("\"${s.name}\" will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(s)
                    pendingDelete = null
                }) { Text("Delete", color = Color(0xFFD64545)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SubjectRow(
    subject: SubjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Compute the swatch colour once per subject — not per recomposition
    val swatch = remember(subject.colourHex) { parseHexColor(subject.colourHex) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF173B67), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(swatch, CircleShape)
        )
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                subject.name,
                color = Color(0xFFE8EEF6),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val meta = listOfNotNull(subject.code, subject.lecturer).joinToString(" • ")
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