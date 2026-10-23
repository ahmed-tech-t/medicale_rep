package com.example.medicalrep.ui.screens.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

private val LogoSize = 80.dp

/** Brand logo box, app title and tagline. */
@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    val logoShape = RoundedCornerShape(radii.pill)
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(LogoSize)
                .shadow(spacing.spaceSmallPlus, logoShape, ambientColor = Brand, spotColor = Brand)
                .background(Brand, logoShape),
            contentAlignment = Alignment.Center,
        ) {
            NexoraText(
                text = stringResource(R.string.login_logo_initials),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        NexoraText(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Ink,
            modifier = Modifier.padding(top = spacing.spaceMedium),
        )
        NexoraText(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = BrandSoft,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun LoginHeaderPreview() {
    MedicalRepTheme { LoginHeader() }
}
