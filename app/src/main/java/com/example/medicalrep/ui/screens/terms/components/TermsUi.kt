package com.example.medicalrep.ui.screens.terms.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.terms.main.TermsEvents
import com.example.medicalrep.ui.screens.terms.main.TermsState
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate900

/** Terms and conditions: intro, last-updated date and one numbered card per term. */
@Composable
fun TermsUi(
    state: TermsState,
    onEvent: (TermsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate25),
    ) {
        AppHeader(
            title = stringResource(R.string.terms_and_conditions),
            onBack = { onEvent(TermsEvents.Back) },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            item(key = "intro") {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
                    NexoraText(
                        text = stringResource(R.string.terms_intro),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900,
                    )
                    NexoraText(
                        text = stringResource(R.string.terms_last_updated),
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500,
                    )
                }
            }
            itemsIndexed(items = state.sections, key = { _, section -> section.titleRes }) { index, section ->
                TermsSectionCard(number = index + 1, section = section)
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 900)
@Composable
private fun TermsUiArabicPreview() {
    MedicalRepTheme { TermsUi(state = TermsState(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun TermsUiEnglishPreview() {
    MedicalRepTheme { TermsUi(state = TermsState(), onEvent = {}) }
}
