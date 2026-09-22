package com.example.studyflow.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyflow.StudyFlowApp
import com.example.studyflow.ui.components.StudyFlowLogo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyFlowApp

    val glowAlpha = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.85f) }
    val nameAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        glowAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, delayMillis = 80, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(650, delayMillis = 120))
        logoScale.animateTo(1f, tween(650, delayMillis = 120))
    }
    LaunchedEffect(Unit) {
        delay(400)
        nameAlpha.animateTo(1f, tween(500))
    }
    LaunchedEffect(Unit) {
        delay(550)
        taglineAlpha.animateTo(0.75f, tween(500))
    }

    LaunchedEffect(Unit) {
        delay(1600)
        // Local-only session check
        if (app.sessionManager.isLoggedIn()) onNavigateToHome()
        else onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F2744),
                        Color(0xFF173B67),
                        Color(0xFF1E4D3A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .alpha(glowAlpha.value)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x332E9B68),
                                    Color(0x00173B67)
                                )
                            )
                        )
                )
                StudyFlowLogo(
                    modifier = Modifier
                        .alpha(logoAlpha.value)
                        .scale(logoScale.value),
                    size = 150.dp
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "StudyFlow",
                color = Color(0xFFE8EEF6),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(nameAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "PLAN · STUDY · ACHIEVE",
                color = Color(0xFFE8EEF6),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}