package com.appcore.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class CornerRadii(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
    val pill: Dp,
    val sheet: Dp,
) {
    companion object {
        fun defaults(): CornerRadii = AdaptiveDimensions
            .from(DeviceConfiguration.Mobile_Portrait)
            .toCornerRadii()
    }
}

fun AdaptiveDimensions.toCornerRadii(): CornerRadii {
    val scale = when (device) {
        DeviceConfiguration.Mobile_Portrait,
        DeviceConfiguration.Mobile_Landscape,
            -> 1f

        DeviceConfiguration.Tablet_Portrait -> 1.15f
        DeviceConfiguration.Tablet_Landscape -> 1.25f
        DeviceConfiguration.Desktop -> 1.35f
    }
    return CornerRadii(
        extraSmall = (4f * scale).dp,
        small = (8f * scale).dp,
        medium = (10f * scale).dp,
        large = (12f * scale).dp,
        extraLarge = (16f * scale).dp,
        pill = (20f * scale).dp,
        sheet = (40f * scale).dp,
    )
}

@Composable
fun roundedShape(radius: Dp): Shape = RoundedCornerShape(radius)


val mediumCornerRedis: Dp
    @Composable
    get() = LocalCornerRadii.current.medium