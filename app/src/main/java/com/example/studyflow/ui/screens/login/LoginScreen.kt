package com.example.studyflow.ui.screens.login

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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyFlowApp
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
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
                "Welcome back",
                color = Color(0xFFE8EEF6),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Sign in to continue your study flow",
                color = Color(0xFFE8EEF6).copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(32.dp))

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
            Spacer(Modifier.height(12.dp))
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

            generalError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFFD64545), fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    emailError = null; passwordError = null; generalError = null

                    val emailOk = android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim()).matches()
                    if (!emailOk) emailError = "Please enter a valid email address"
                    if (password.length < 6) passwordError = "Password must be at least 6 characters"
                    if (!emailOk || password.length < 6) return@Button

                    loading = true
                    scope.launch {
                        val result = app.authRepository.login(email, password)
                        loading = false
                        if (result.success) {
                            app.sessionManager.markLoggedIn(
                                userId = result.userId ?: -1L,
                                email = email.trim().lowercase()
                            )
                            onLoginSuccess()
                        } else {
                            generalError = result.message ?: "Login failed"
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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
                    Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onGoToRegister) {
                Text(
                    "Don't have an account? Register",
                    color = Color(0xFFE8EEF6).copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}