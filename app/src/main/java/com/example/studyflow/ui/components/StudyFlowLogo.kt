package com.example.studyflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun StudyFlowLogo(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 140.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Rounded square backdrop
        drawRoundRect(
            color = Color(0xFF173B67),
            size = Size(w, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.18f)
        )

        // Graduation cap (top diamond)
        val capTop = Path().apply {
            moveTo(w * 0.50f, h * 0.26f)
            lineTo(w * 0.78f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.50f)
            lineTo(w * 0.22f, h * 0.38f)
            close()
        }
        drawPath(capTop, color = Color.White)

        // Cap base
        val capBase = Path().apply {
            moveTo(w * 0.36f, h * 0.42f)
            lineTo(w * 0.36f, h * 0.58f)
            cubicTo(
                w * 0.36f, h * 0.64f,
                w * 0.64f, h * 0.64f,
                w * 0.64f, h * 0.58f
            )
            lineTo(w * 0.64f, h * 0.42f)
            lineTo(w * 0.50f, h * 0.49f)
            close()
        }
        drawPath(capBase, color = Color.White)

        // Flowing green checkmark
        val flow = Path().apply {
            moveTo(w * 0.36f, h * 0.76f)
            cubicTo(
                w * 0.45f, h * 0.68f,
                w * 0.55f, h * 0.68f,
                w * 0.64f, h * 0.60f
            )
            cubicTo(
                w * 0.68f, h * 0.56f,
                w * 0.71f, h * 0.52f,
                w * 0.74f, h * 0.48f
            )
        }
        drawPath(
            path = flow,
            color = Color(0xFF2E9B68),
            style = Stroke(width = w * 0.06f)
        )

        // Flow dot / end
        drawCircle(
            color = Color(0xFF2E9B68),
            radius = w * 0.045f,
            center = Offset(w * 0.76f, h * 0.50f)
        )
    }
}