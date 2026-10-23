package com.appcore.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AdaptiveDimensions(
    val device: DeviceConfiguration,
    val screenPaddingHorizontal: Dp,
    val screenPaddingVertical: Dp,
    val contentMaxWidth: Dp?,
    val gridMinItemWidth: Dp,
    val listItemSpacing: Dp,
    val cardPadding: Dp,
    val sectionSpacing: Dp,
    val miniIconSize: Dp,
    val iconSize: Dp,
    val smallIconSize: Dp,
    val largeIconSize: Dp,
    val iconButtonSize: Dp,
    val primaryButtonHeight: Dp,
    val thumbnailSize: Dp,
    val horizontalCardWidth: Dp,
    val horizontalCardImageHeight: Dp,
    val propertyCardWidth: Dp,
    val propertyCardMinWidth: Dp,
    val developerAvatarSize: Dp,
    val developerCardLogoSize: Dp,
    val categoryItemBackgroundSize: Dp,
    val illustrationSize: Dp,
) {
    companion object {
        fun from(device: DeviceConfiguration): AdaptiveDimensions {
            return when (device) {
                DeviceConfiguration.Mobile_Portrait,
                DeviceConfiguration.Mobile_Landscape,
                    -> AdaptiveDimensions(
                    device = device,
                    screenPaddingHorizontal = 16.dp,
                    screenPaddingVertical = 16.dp,
                    contentMaxWidth = null,
                    gridMinItemWidth = 320.dp,
                    listItemSpacing = 16.dp,
                    cardPadding = 24.dp,
                    sectionSpacing = 24.dp,
                    iconSize = 24.dp,
                    miniIconSize = 14.dp,
                    smallIconSize = 20.dp,
                    largeIconSize = 30.dp,
                    primaryButtonHeight = 48.dp,
                    thumbnailSize = 40.dp,
                    iconButtonSize = 48.dp,
                    horizontalCardWidth = 180.dp,
                    horizontalCardImageHeight = 110.dp,
                    propertyCardWidth = 300.dp,
                    propertyCardMinWidth = 260.dp,
                    developerAvatarSize = 56.dp,
                    developerCardLogoSize = 56.dp,
                    categoryItemBackgroundSize = 68.dp,
                    illustrationSize = 160.dp,
                )

                DeviceConfiguration.Tablet_Portrait -> AdaptiveDimensions(
                    device = device,
                    screenPaddingHorizontal = 16.dp,
                    screenPaddingVertical = 24.dp,
                    contentMaxWidth = null,
                    gridMinItemWidth = 480.dp,
                    listItemSpacing = 20.dp,
                    cardPadding = 28.dp,
                    sectionSpacing = 28.dp,
                    iconSize = 28.dp,
                    miniIconSize = 16.dp,
                    smallIconSize = 24.dp,
                    largeIconSize = 32.dp,
                    primaryButtonHeight = 52.dp,
                    thumbnailSize = 56.dp,
                    iconButtonSize = 56.dp,
                    horizontalCardWidth = 208.dp,
                    horizontalCardImageHeight = 128.dp,
                    propertyCardWidth = 340.dp,
                    propertyCardMinWidth = 280.dp,
                    developerAvatarSize = 93.dp,
                    developerCardLogoSize = 64.dp,
                    categoryItemBackgroundSize = 80.dp,
                    illustrationSize = 180.dp,
                )

                DeviceConfiguration.Tablet_Landscape -> AdaptiveDimensions(
                    device = device,
                    screenPaddingHorizontal = 16.dp,
                    screenPaddingVertical = 24.dp,
                    contentMaxWidth = null,
                    gridMinItemWidth = 380.dp,
                    listItemSpacing = 24.dp,
                    cardPadding = 28.dp,
                    sectionSpacing = 28.dp,
                    miniIconSize = 20.dp,
                    iconSize = 32.dp,
                    smallIconSize = 28.dp,
                    largeIconSize = 40.dp,
                    primaryButtonHeight = 52.dp,
                    thumbnailSize = 56.dp,
                    iconButtonSize = 56.dp,
                    horizontalCardWidth = 225.dp,
                    horizontalCardImageHeight = 138.dp,
                    propertyCardWidth = 360.dp,
                    propertyCardMinWidth = 300.dp,
                    developerAvatarSize = 100.dp,
                    developerCardLogoSize = 72.dp,
                    categoryItemBackgroundSize = 88.dp,
                    illustrationSize = 200.dp,
                )

                DeviceConfiguration.Desktop -> AdaptiveDimensions(
                    device = device,
                    screenPaddingHorizontal = 16.dp,
                    screenPaddingVertical = 32.dp,
                    contentMaxWidth = null,
                    gridMinItemWidth = 400.dp,
                    listItemSpacing = 24.dp,
                    cardPadding = 32.dp,
                    sectionSpacing = 32.dp,
                    miniIconSize = 24.dp,
                    iconSize = 34.dp,
                    smallIconSize = 30.dp,
                    largeIconSize = 42.dp,
                    primaryButtonHeight = 56.dp,
                    thumbnailSize = 64.dp,
                    iconButtonSize = 64.dp,
                    horizontalCardWidth = 243.dp,
                    horizontalCardImageHeight = 149.dp,
                    propertyCardWidth = 380.dp,
                    propertyCardMinWidth = 320.dp,
                    developerAvatarSize = 107.dp,
                    developerCardLogoSize = 80.dp,
                    categoryItemBackgroundSize = 96.dp,
                    illustrationSize = 220.dp,
                )
            }
        }
    }
}

fun AdaptiveDimensions.toSpacingDimensions(): Dimensions {
    val scale = when (device) {
        DeviceConfiguration.Mobile_Portrait,
        DeviceConfiguration.Mobile_Landscape,
            -> 1f

        DeviceConfiguration.Tablet_Portrait -> 1.15f
        DeviceConfiguration.Tablet_Landscape -> 1.25f
        DeviceConfiguration.Desktop -> 1.35f
    }
    return Dimensions(
        spaceExtraExtraSmall = (2f * scale).dp,
        spaceExtraSmall = (4f * scale).dp,
        spaceSmall = (8f * scale).dp,
        spaceSmallPlus = (12f * scale).dp,
        spaceMedium = screenPaddingHorizontal,
        spaceMediumPlus = (25f * scale).dp,
        spaceLarge = (32f * scale).dp,
        spaceLargePlus = (40f * scale).dp,
        spaceExtraLarge = (64f * scale).dp,
    )
}
