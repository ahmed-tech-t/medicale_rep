package com.appcore.core.ui.root

sealed interface RootEvents {
    data object RetryBootstrap : RootEvents
}
