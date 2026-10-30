package com.example.medicalrep.ui.screens.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.widgets.PreviewRtlLtr
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate300

@Composable
fun ProgressBarComponent(
    modifier: Modifier = Modifier,
    percentage: Float,
    appLanguage: String = if (LocalLayoutDirection.current == LayoutDirection.Rtl) "ar" else "en"
) {
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = appLanguage == "ar" || (layoutDirection == LayoutDirection.Rtl && appLanguage != "en")

    val animatePercentage = remember { Animatable(0f) }
    LaunchedEffect(percentage) {
        animatePercentage.animateTo(
            targetValue = percentage,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.spaceSmall)
            ) {
                if (isRtl) {
                    drawContext.canvas.translate(size.width, 0f)
                    // Flip the canvas horizontally
                    drawContext.canvas.scale(-1f, 1f)
                }
                drawRoundRect(color = Slate300, cornerRadius = CornerRadius(30f, 30f))
                drawRoundRect(
                    color = Brand,
                    cornerRadius = CornerRadius(30f, 30f),
                    size = Size(size.width * animatePercentage.value, size.height)
                )
            }
        }
    }
}

@PreviewRtlLtr
@Composable
private fun ProgressBarComponentPreview() {
    MedicalRepTheme {
        ProgressBarComponent(
            percentage = 0.75f,
            modifier = Modifier.padding(16.dp)
        )
    }
}
