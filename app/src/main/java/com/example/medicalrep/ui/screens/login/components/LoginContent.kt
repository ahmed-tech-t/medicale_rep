package com.example.medicalrep.ui.screens.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.components.modifier_ext.notchesPadding
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraOutlinedSurface
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.screens.login.main.LoginEvents
import com.example.medicalrep.ui.screens.login.main.LoginState
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate600

@Composable
fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .notchesPadding()
            .padding(
                horizontal = dim.screenPaddingHorizontal,
                vertical = dim.screenPaddingVertical
            ),
        verticalArrangement = Arrangement.spacedBy(dim.listItemSpacing)
    ) {
        item {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(spacing.spaceLarge)
            ) {
                LoginLanguagePill(
                    code = state.otherLanguageCode,
                    onClick = { onEvent(LoginEvents.LanguageClicked) },
                )
                LoginHeader()
            }
        }
        item {
            Spacer(Modifier.height(spacing.spaceMedium))
        }
        item {
            NexoraTextField(
                state = state.companyCode,
                label = stringResource(R.string.login_company_code),
                placeholder = stringResource(R.string.enter_company_code),
                isError = state.showErrors && state.companyCodeBlank,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        item {
            NexoraTextField(
                state = state.username,
                label = stringResource(R.string.login_username),
                placeholder = stringResource(R.string.enter_username),

                isError = state.showErrors && state.usernameBlank,
            )
        }
        item {
            NexoraTextField(
                state = state.password,
                label = stringResource(R.string.login_password),
                isPassword = true,
                placeholder = stringResource(R.string.enter_password),

                isError = state.showErrors && state.passwordBlank,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        }
        item {
            NexoraOutlinedSurface {
                NexoraText(
                    text = stringResource(R.string.login_device_notice),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                    color = Slate600,
                )
            }
        }
        item {
            NexoraButton(
                text = stringResource(R.string.login_sign_in),
                onClick = { onEvent(LoginEvents.SignInClicked) },
                enabled = state.buttonEnabled
                )
        }
    }
}

@Preview(showBackground = true, heightDp = 853, widthDp = 374, locale = "ar")
@Composable
private fun LoginContentArabicPreview() {
    MedicalRepTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LoginContent(state = LoginState(), onEvent = {})
        }

    }
}

@Preview(showBackground = true, heightDp = 853, widthDp = 374)
@Composable
private fun LoginContentErrorPreview() {
    MedicalRepTheme { LoginContent(state = LoginState(showErrors = true), onEvent = {}) }
}
