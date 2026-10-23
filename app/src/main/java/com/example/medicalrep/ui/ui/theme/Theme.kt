package com.example.medicalrep.ui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.appcore.core.ui.theme.AdaptiveDimensions
import com.appcore.core.ui.theme.DeviceConfiguration
import com.appcore.core.ui.theme.LocalAdaptiveDimensions
import com.appcore.core.ui.theme.LocalCornerRadii
import com.appcore.core.ui.theme.LocalSpacing
import com.appcore.core.ui.theme.toCornerRadii
import com.appcore.core.ui.theme.toSpacingDimensions

private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BrandTint,
    onPrimaryContainer = BrandDeep,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    outlineVariant = Slate300,
    error = Error,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MedicalRepTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(widthSizeClass)
    val adaptiveDimensions = AdaptiveDimensions.from(deviceConfiguration)
    val spacing = adaptiveDimensions.toSpacingDimensions()
    val cornerRadii = adaptiveDimensions.toCornerRadii()

    CompositionLocalProvider(
        LocalAdaptiveDimensions provides adaptiveDimensions,
        LocalSpacing provides spacing,
        LocalCornerRadii provides cornerRadii,
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,

            content = content
        )
    }
}
