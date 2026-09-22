package com.example.studyflow.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Parses a #RRGGBB or #AARRGGBB hex string into a Compose [Color].
 * Falls back to StudyFlow green on parse error so a bad value never crashes the UI.
 */
fun parseHexColor(hex: String, fallback: Color = Color(0xFF2E9B68)): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(fallback)