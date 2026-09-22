package com.example.studyflow.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyflow.StudyFlowApp
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyFlowApp
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F2744), Color(0xFF173B67))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create your account",
                color = Color(0xFFE8EEF6),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Start organising your studies today",
                color = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E9B68),
                    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
                    focusedLabelColor = Color(0xFF2E9B68),
                    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                    focusedTextColor = Color(0xFFE8EEF6),
                    unfocusedTextColor = Color(0xFFE8EEF6)
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email address") },
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E9B68),
                    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
                    focusedLabelColor = Color(0xFF2E9B68),
                    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                    focusedTextColor = Color(0xFFE8EEF6),
                    unfocusedTextColor = Color(0xFFE8EEF6)
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E9B68),
                    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
                    focusedLabelColor = Color(0xFF2E9B68),
                    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                    focusedTextColor = Color(0xFFE8EEF6),
                    unfocusedTextColor = Color(0xFFE8EEF6)
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; confirmError = null },
                label = { Text("Confirm password") },
                isError = confirmError != null,
                supportingText = confirmError?.let { { Text(it) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E9B68),
                    unfocusedBorderColor = Color(0xFFE8EEF6).copy(alpha = 0.4f),
                    focusedLabelColor = Color(0xFF2E9B68),
                    unfocusedLabelColor = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                    focusedTextColor = Color(0xFFE8EEF6),
                    unfocusedTextColor = Color(0xFFE8EEF6)
                )
            )

            generalError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFFD64545), fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    emailError = null; passwordError = null; confirmError = null
                    generalError = null

                    val emailOk = android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim()).matches()
                    if (!emailOk) emailError = "Please enter a valid email address"
                    if (password.length < 6) passwordError = "Password must be at least 6 characters"
                    if (password != confirm) confirmError = "Passwords do not match"
                    if (!emailOk || password.length < 6 || password != confirm) return@Button

                    loading = true
                    scope.launch {
                        val result = app.authRepository.register(
                            email = email,
                            password = password,
                            displayName = displayName.ifBlank { null }
                        )
                        if (result.success) {
                            // Auto-login after register
                            val login = app.authRepository.login(email, password)
                            loading = false
                            if (login.success) {
                                app.sessionManager.markLoggedIn(
                                    userId = login.userId ?: -1L,
                                    email = email.trim().lowercase()
                                )
                                onRegisterSuccess()
                            } else {
                                generalError = login.message ?: "Auto-login failed"
                            }
                        } else {
                            loading = false
                            generalError = result.message ?: "Registration failed"
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E9B68),
                    contentColor = Color.White
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(20.dp)
                    )
                } else {
                    Text("Create account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onGoToLogin) {
                Text(
                    "Already have an account? Log in",
                    color = Color(0xFFE8EEF6).copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}