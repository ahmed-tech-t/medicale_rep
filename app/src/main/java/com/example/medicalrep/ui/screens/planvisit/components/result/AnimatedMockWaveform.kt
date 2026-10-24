package com.example.medicalrep.ui.screens.planvisit.components.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import kotlin.math.sin


import kotlin.math.abs

@Composable
fun AnimatedMockWaveform(
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
    barGap: Dp = 3.dp,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Increased duration to 1500ms so the "speech pauses" don't fly by too fast
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val canvasCenterY = size.height / 2
        val barWidthPx = barWidth.toPx()
        val barGapPx = barGap.toPx()
        val step = barWidthPx + barGapPx
        val barCount = (size.width / step).toInt()

        var startX = 0f

        for (i in 0 until barCount) {
            // 1. Slow envelope: Creates wide gaps (mimicking pauses in speech)
            val gapEnvelope = sin(phase + i * 0.1).toFloat()

            // 2. Fast ripple: Creates the erratic bouncing peaks within the speech bursts
            val ripple = sin(phase * 3f + i * 0.5).toFloat()

            // Multiply them together. Squaring the gapEnvelope makes the "silence" gaps wider and flatter.
            val combined = abs(ripple * (gapEnvelope * gapEnvelope))

            // Add a small baseline (0.1f) so the gaps still show a tiny dot instead of completely disappearing
            val normalizedAmp = (combined * 1.3f + 0.1f).coerceIn(0.1f, 1f)
            val barHeight = size.height * normalizedAmp

            drawRoundRect(
                color = barColor,
                topLeft = Offset(
                    x = startX,
                    y = canvasCenterY - (barHeight / 2)
                ),
                size = Size(width = barWidthPx, height = barHeight),
                cornerRadius = CornerRadius(barWidthPx / 2, barWidthPx / 2)
            )

            startX += step
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun AnimatedMockWaveformPreview() {
    MedicalRepTheme {
        AnimatedMockWaveform(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(16.dp)
        )
    }
}