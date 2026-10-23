package com.appcore.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val default: Dp = 0.dp,
    val spaceExtraExtraSmall: Dp = 2.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceSmallPlus: Dp = 12.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceMediumPlus: Dp = 25.dp,
    val spaceLarge: Dp = 32.dp,
    val spaceLargePlus: Dp = 40.dp,
    val spaceExtraLarge: Dp = 64.dp,
)

val LocalSpacing = staticCompositionLocalOf { Dimensions() }

val LocalAdaptiveDimensions = staticCompositionLocalOf {
    AdaptiveDimensions.from(DeviceConfiguration.Mobile_Portrait)
}

val LocalCornerRadii = staticCompositionLocalOf { CornerRadii.defaults() }

val dim: AdaptiveDimensions
    @Composable @ReadOnlyComposable get() = LocalAdaptiveDimensions.current

val spacing: Dimensions
    @Composable @ReadOnlyComposable get() = LocalSpacing.current

val radii: CornerRadii
    @Composable @ReadOnlyComposable get() = LocalCornerRadii.current

val dynamicPadding: Dp
    @Composable
    get() = LocalAdaptiveDimensions.current.screenPaddingHorizontal

val iconSize: Dp
    @Composable
    get() = LocalAdaptiveDimensions.current.iconSize

val cornerExtraLarge: Dp
    @Composable
    get() = LocalCornerRadii.current.extraLarge


val smallIconSize: Dp
    @Composable
    get() = LocalAdaptiveDimensions.current.smallIconSize

val miniIconSize: Dp
    @Composable
    get() = LocalAdaptiveDimensions.current.miniIconSize