package com.example.studyflow.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyflow.data.local.TaskEntity
import com.example.studyflow.data.local.TaskPriority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    existing: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String?, subject: String?, dueDate: Long?, priority: TaskPriority) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var subject by remember { mutableStateOf(existing?.subject ?: "") }
    var dueDate by remember { mutableStateOf(existing?.dueDate) }
    var priority by remember { mutableStateOf(existing?.priority ?: TaskPriority.MEDIUM) }
    var titleError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F2744),
        contentColor = Color(0xFFE8EEF6)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                if (existing == null) "New task" else "Edit task",
                color = Color(0xFFE8EEF6),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = null },
                label = { Text("Title") },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = sheetFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            Text("Priority", color = Color(0xFFE8EEF6).copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = {
                            Text(
                                p.name.lowercase().replaceFirstChar { it.uppercase() }
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

            Spacer(Modifier.height(16.dp))

            Text("Due date", color = Color(0xFFE8EEF6).copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipButton(
                    label = "Today",
                    selected = dueDate != null && isSameDay(dueDate!!, todayEpoch())
                ) { dueDate = todayEpoch() }

                ChipButton(
                    label = "Tomorrow",
                    selected = dueDate != null && isSameDay(dueDate!!, tomorrowEpoch())
                ) { dueDate = tomorrowEpoch() }

                ChipButton(
                    label = "+1 week",
                    selected = false
                ) { dueDate = inOneWeekEpoch() }

                if (dueDate != null) {
                    TextButton(onClick = { dueDate = null }) {
                        Text("Clear", color = Color(0xFFD64545))
                    }
                }
            }

            if (dueDate != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Due ${formatDue(dueDate!!)}",
                    color = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Color(0xFFE8EEF6))
                }
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = "Title is required"
                            return@Button
                        }
                        onSave(
                            title.trim(),
                            description.ifBlank { null },
                            subject.ifBlank { null },
                            dueDate,
                            priority
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E9B68),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (existing == null) "Add" else "Save")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF2E9B68),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF173B67),
            labelColor = Color(0xFFE8EEF6)
        )
    )
}

@Composable
private fun sheetFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF2E9B68),
    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
    focusedLabelColor = Color(0xFF2E9B68),
    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
    focusedTextColor = Color(0xFFE8EEF6),
    unfocusedTextColor = Color(0xFFE8EEF6),
    cursorColor = Color(0xFF2E9B68)
)

private fun todayEpoch(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun tomorrowEpoch(): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun inOneWeekEpoch(): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 7)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun isSameDay(a: Long, b: Long): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(Date(a)) == fmt.format(Date(b))
}

private fun formatDue(epochMillis: Long): String =
    SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(epochMillis))