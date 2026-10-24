package com.example.medicalrep.ui.components.chrome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Brand-colored top bar with a back arrow and a title, shared by full-screen flows. */
@Composable
fun AppHeader(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Brand)
            .statusBarsPadding()
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraIconButton(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            tint = Slate50,
        )
        NexoraText(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate50,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AppHeaderArabicPreview() {
    MedicalRepTheme { AppHeader(title = "مركز البيانات والمزامنة", onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AppHeaderEnglishPreview() {
    MedicalRepTheme { AppHeader(title = "Data center & sync", onBack = {}) }
}
