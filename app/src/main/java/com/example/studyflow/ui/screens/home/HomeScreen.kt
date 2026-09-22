package com.example.studyflow.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.ui.screens.tasks.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onOpenTasks: () -> Unit,
    onOpenSubjects: () -> Unit,
    onOpenStudy: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    taskVm: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyFlowApp
    val scope = rememberCoroutineScope()

    val pending by taskVm.pendingCount.collectAsState()
    val completed by taskVm.completedCount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF0F2744), Color(0xFF173B67))))
            .padding(24.dp)
    ) {
        Column(Modifier.fillMaxSize()) {

            Spacer(Modifier.height(24.dp))
            Text("Welcome back", color = Color(0xFFE8EEF6).copy(alpha = 0.7f), fontSize = 14.sp)
            Text(
                app.sessionManager.currentEmail() ?: "Student",
                color = Color(0xFFE8EEF6),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Pending", "$pending", Modifier.weight(1f))
                StatTile("Completed", "$completed", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            NavButton("Tasks", onOpenTasks)
            Spacer(Modifier.height(10.dp))
            NavButton("Subjects", onOpenSubjects)
            Spacer(Modifier.height(10.dp))
            NavButton("Study Sessions", onOpenStudy)
            Spacer(Modifier.height(10.dp))
            NavButton("Settings", onOpenSettings)

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        app.authRepository.logout()
                        app.sessionManager.clear()
                        onLogout()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Log out", color = Color(0xFFE8EEF6)) }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF173B67), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(label, color = Color(0xFFE8EEF6).copy(alpha = 0.7f), fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color(0xFFE8EEF6), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NavButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2E9B68),
            contentColor = Color.White
        )
    ) { Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
}