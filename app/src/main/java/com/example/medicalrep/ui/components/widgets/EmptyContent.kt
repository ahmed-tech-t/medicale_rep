package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraImage
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

@Composable
fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NexoraImage(painter = painterResource(R.drawable.ic_doc))
        NexoraText(
            text = stringResource(R.string.thier_is_no_result),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@PreviewRtlLtr
@Composable
private fun EmptyContentPreview() {
    MedicalRepTheme {
        EmptyContent()
    }
}