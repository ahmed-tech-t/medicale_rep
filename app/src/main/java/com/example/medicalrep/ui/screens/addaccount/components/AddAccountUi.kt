package com.example.medicalrep.ui.screens.addaccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountEvents
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.addaccount.utils.AddAccountFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25

/** Figma 170:649: register a new doctor / account. */
@Composable
fun AddAccountUi(
    state: AddAccountState,
    onEvent: (AddAccountEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate25),
    ) {
        AppHeader(
            title = stringResource(R.string.add_account_title),
            onBack = { onEvent(AddAccountEvents.Back) },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            LocationCard(state, onEvent)
            EntityTypeCard(state, onEvent)
            NameCard(state, onEvent)
            GpsPinCard(state, onEvent)
            SignPhotoCard(
                state = state,
                onPhotoCaptured = { onEvent(AddAccountEvents.SignPhotoCaptured(it)) },
            )
        }
        NexoraButton(
            text = stringResource(R.string.add_account_save),
            onClick = { onEvent(AddAccountEvents.Save) },
            enabled = state.canSave,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 1000)
@Composable
private fun AddAccountUiArabicPreview() {
    MedicalRepTheme { AddAccountUi(state = AddAccountFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun AddAccountUiEnglishPreview() {
    MedicalRepTheme { AddAccountUi(state = AddAccountFakes.state(), onEvent = {}) }
}
