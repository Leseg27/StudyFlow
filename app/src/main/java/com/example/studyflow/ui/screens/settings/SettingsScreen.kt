package com.example.studyflow.ui.screens.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyFlowApp
    val currentEmail = app.sessionManager.currentEmail() ?: ""

    var displayName by remember { mutableStateOf("") }
    var dailyGoal by remember { mutableStateOf(60) }
    var language by remember { mutableStateOf("en") }
    var theme by remember { mutableStateOf("dark") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color(0xFFE8EEF6), fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0F2744), Color(0xFF173B67))))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text("Account", color = Color(0xFFE8EEF6), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(currentEmail, color = Color(0xFFE8EEF6).copy(alpha = 0.7f), fontSize = 13.sp)

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors()
                )

                Spacer(Modifier.height(16.dp))

                Text("Daily study goal (minutes)", color = Color(0xFFE8EEF6), fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60, 90, 120).forEach { g ->
                        FilterChip(
                            selected = dailyGoal == g,
                            onClick = { dailyGoal = g },
                            label = { Text("$g") },
                            colors = chipColors()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Language", color = Color(0xFFE8EEF6), fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en" to "English", "zu" to "isiZulu", "st" to "Sesotho").forEach { (k, label) ->
                        FilterChip(
                            selected = language == k,
                            onClick = { language = k },
                            label = { Text(label) },
                            colors = chipColors()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Theme", color = Color(0xFFE8EEF6), fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("dark", "light").forEach { t ->
                        FilterChip(
                            selected = theme == t,
                            onClick = { theme = t },
                            label = { Text(t.replaceFirstChar { it.uppercase() }) },
                            colors = chipColors()
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        vm.updateProfile(displayName, language, dailyGoal, theme) { onSaved() }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E9B68),
                        contentColor = Color.White
                    )
                ) { Text("Save", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF2E9B68),
    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
    focusedLabelColor = Color(0xFF2E9B68),
    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
    focusedTextColor = Color(0xFFE8EEF6),
    unfocusedTextColor = Color(0xFFE8EEF6),
    cursorColor = Color(0xFF2E9B68)
)

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Color(0xFF2E9B68),
    selectedLabelColor = Color.White,
    containerColor = Color(0xFF173B67),
    labelColor = Color(0xFFE8EEF6)
)