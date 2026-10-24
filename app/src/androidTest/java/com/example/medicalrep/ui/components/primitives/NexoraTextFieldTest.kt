package com.example.medicalrep.ui.components.primitives

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Rule
import org.junit.Test

class NexoraTextFieldTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun eyeIcon(@StringRes description: Int) =
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(description), useUnmergedTree = true)

    @Test
    fun passwordField_eyeIconTogglesBetweenShowAndHide() {
        composeRule.setContent {
            MedicalRepTheme {
                NexoraTextField(state = rememberTextFieldState("secret"), isPassword = true)
            }
        }

        eyeIcon(R.string.cd_show_password).assertIsDisplayed().performClick()
        eyeIcon(R.string.cd_hide_password).assertIsDisplayed().performClick()
        eyeIcon(R.string.cd_show_password).assertIsDisplayed()
    }

    @Test
    fun nonPasswordField_hasNoEyeIcon() {
        composeRule.setContent {
            MedicalRepTheme {
                NexoraTextField(state = rememberTextFieldState("ahmed"))
            }
        }

        eyeIcon(R.string.cd_show_password).assertDoesNotExist()
    }
}
