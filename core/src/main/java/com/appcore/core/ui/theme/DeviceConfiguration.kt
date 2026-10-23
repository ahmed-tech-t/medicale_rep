package com.appcore.core.ui.theme

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

sealed interface DeviceConfiguration {
    object Mobile_Portrait : DeviceConfiguration
    object Mobile_Landscape : DeviceConfiguration
    object Tablet_Portrait : DeviceConfiguration
    object Tablet_Landscape : DeviceConfiguration
    object Desktop : DeviceConfiguration

    companion object {
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            val widthClass = windowSizeClass.windowWidthSizeClass
            val heightClass = windowSizeClass.windowHeightSizeClass
            return when (widthClass) {
                WindowWidthSizeClass.COMPACT if heightClass == WindowHeightSizeClass.MEDIUM -> Mobile_Portrait
                WindowWidthSizeClass.COMPACT if heightClass == WindowHeightSizeClass.EXPANDED -> Mobile_Portrait
                WindowWidthSizeClass.EXPANDED if heightClass == WindowHeightSizeClass.COMPACT -> Mobile_Landscape
                WindowWidthSizeClass.MEDIUM if heightClass == WindowHeightSizeClass.EXPANDED -> Tablet_Portrait
                WindowWidthSizeClass.EXPANDED if heightClass == WindowHeightSizeClass.MEDIUM -> Tablet_Landscape
                else -> Desktop
            }
        }
    }
}
