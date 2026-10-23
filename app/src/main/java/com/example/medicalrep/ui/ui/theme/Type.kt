package com.example.medicalrep.ui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.medicalrep.R

@OptIn(ExperimentalTextApi::class)
private fun cairo(weight: FontWeight) = Font(
    resId = R.font.cairo,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Cairo = FontFamily(
    cairo(FontWeight.Normal),
    cairo(FontWeight.SemiBold),
    cairo(FontWeight.Bold),
    cairo(FontWeight.ExtraBold),
    cairo(FontWeight.Black)
)

private fun cairoStyle(size: Float, lineHeight: Float, weight: FontWeight) = TextStyle(
    fontFamily = Cairo,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp
)

private val Default = Typography()

val Typography = Typography(
    displayLarge = Default.displayLarge.copy(fontFamily = Cairo),
    displayMedium = Default.displayMedium.copy(fontFamily = Cairo),
    displaySmall = Default.displaySmall.copy(fontFamily = Cairo),
    headlineLarge = cairoStyle(30f, 36f, FontWeight.ExtraBold),
    headlineMedium = Default.headlineMedium.copy(fontFamily = Cairo),
    headlineSmall = cairoStyle(24f, 32f, FontWeight.Bold),
    titleLarge = cairoStyle(20f, 28f, FontWeight.ExtraBold),
    titleMedium = cairoStyle(16f, 24f, FontWeight.Bold),
    titleSmall = cairoStyle(13f, 20f, FontWeight.ExtraBold),
    bodyLarge = cairoStyle(14f, 20f, FontWeight.Normal),
    bodyMedium = cairoStyle(12f, 16f, FontWeight.Bold),
    bodySmall = cairoStyle(11f, 16.5f, FontWeight.Bold),
    labelLarge = cairoStyle(12f, 16f, FontWeight.ExtraBold),
    labelMedium = cairoStyle(10.5f, 16f, FontWeight.SemiBold),
    labelSmall = cairoStyle(10f, 15f, FontWeight.Normal)
)
