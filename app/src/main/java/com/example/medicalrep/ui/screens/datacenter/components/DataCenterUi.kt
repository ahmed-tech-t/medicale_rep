package com.example.medicalrep.ui.screens.datacenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.medicalrep.ui.screens.datacenter.main.DataCenterEvents
import com.example.medicalrep.ui.screens.datacenter.main.DataCenterState
import com.example.medicalrep.ui.screens.datacenter.utils.DataCenterFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900

/** Figma 90:1772: offline data sync center with one card per data category. */
@Composable
fun DataCenterUi(
    state: DataCenterState,
    onEvent: (DataCenterEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate25),
    ) {
        AppHeader(
            title = stringResource(R.string.data_center_title),
            onBack = { onEvent(DataCenterEvents.OnBack) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            item(key = "intro") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
                ) {
                    NexoraText(
                        text = stringResource(R.string.data_center_intro_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900,
                    )
                    NexoraText(
                        text = stringResource(R.string.data_center_intro_body),
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500,
                    )
                }
            }

            item(key = "section") {
                NexoraText(
                    text = stringResource(R.string.data_center_section_server),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Slate600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.spaceExtraSmall),
                )
            }

            items(items = state.items, key = { it.id }) { item ->
                DataCenterSyncCard(
                    item = item,
                    onSync = { onEvent(DataCenterEvents.OnSync(item.id)) },
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 900)
@Composable
private fun DataCenterUiArabicPreview() {
    MedicalRepTheme {
        DataCenterUi(state = DataCenterFakes.state(), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun DataCenterUiEnglishPreview() {
    MedicalRepTheme {
        DataCenterUi(state = DataCenterFakes.state(), onEvent = {})
    }
}
