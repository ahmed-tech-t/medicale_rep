package com.appcore.core.ui.root

import androidx.compose.runtime.Immutable
import com.appcore.core.domain.models.AppBootstrap
import com.appcore.core.domain.utils.Resource

@Immutable
data class RootState(
    val bootstrap: Resource<AppBootstrap> = Resource.Loading(),
)
