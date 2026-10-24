package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.visitdetails.main.VisitDetailsEvents
import com.example.medicalrep.ui.screens.visitdetails.main.VisitDetailsState
import com.example.medicalrep.ui.screens.visitdetails.utils.VisitDetailsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** Figma 302:2216: read-only details of a visit the rep already did. */
@Composable
fun VisitDetailsUi(
    state: VisitDetailsState,
    onEvent: (VisitDetailsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.visit_details_title),
            onBack = { onEvent(VisitDetailsEvents.Back) },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            SectionLabel(stringResource(R.string.visit_details_section_info))
            VisitInfoCard(visit = state.visit)
            VisitTimeCard(visit = state.visit)
            SectionLabel(stringResource(R.string.visit_details_section_location))
            VisitLocationCard(visit = state.visit, onViewOnMap = { onEvent(VisitDetailsEvents.ViewOnMap) })
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    NexoraText(text = text, style = MaterialTheme.typography.labelMedium, color = Slate500)
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitDetailsUiArabicPreview() {
    MedicalRepTheme { VisitDetailsUi(state = VisitDetailsFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun VisitDetailsUiEnglishPreview() {
    MedicalRepTheme { VisitDetailsUi(state = VisitDetailsFakes.state(), onEvent = {}) }
}
