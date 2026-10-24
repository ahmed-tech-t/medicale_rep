package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.appcore.core.ui.theme.dim
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon

@Composable
fun SearchIcon(modifier: Modifier = Modifier) {
    NexoraIcon(
        painter = painterResource(R.drawable.ic_search),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(dim.smallIconSize),
    )
}