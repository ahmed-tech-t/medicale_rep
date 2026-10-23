package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Rule
import org.junit.Test

class NexoraTextFieldTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun eyeIcon(description: String) =
        composeRule.onNodeWithContentDescription(description, useUnmergedTree = true)

    @Test
    fun passwordField_eyeIconTogglesBetweenShowAndHide() {
        composeRule.setContent {
            MedicalRepTheme {
                NexoraTextField(state = rememberTextFieldState("secret"), isPassword = true)
            }
        }

        eyeIcon("Show password").assertIsDisplayed().performClick()
        eyeIcon("Hide password").assertIsDisplayed().performClick()
        eyeIcon("Show password").assertIsDisplayed()
    }

    @Test
    fun nonPasswordField_hasNoEyeIcon() {
        composeRule.setContent {
            MedicalRepTheme {
                NexoraTextField(state = rememberTextFieldState("ahmed"))
            }
        }

        eyeIcon("Show password").assertDoesNotExist()
    }
}
