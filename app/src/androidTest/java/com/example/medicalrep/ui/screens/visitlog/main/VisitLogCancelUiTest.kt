package com.example.medicalrep.ui.screens.visitlog.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medicalrep.R
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitlog.components.VisitLogUi
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The card's Cancel visit sends CancelVisit; the sheet confirms only with a reason, and Go back dismisses it. */
class VisitLogCancelUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<VisitLogEvents>()
    private val reason = TextFieldState()

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setUi(state: VisitLogState) {
        composeRule.setContent {
            MedicalRepTheme {
                VisitLogUi(state = state, visits = pagingItemsOf(VisitLogFakes.requiredVisits()), onEvent = { events += it })
            }
        }
    }

    private fun sheetState() = VisitLogFakes.state().copy(overlay = VisitLogOverlay.CancelVisit("v1"), cancelReason = reason)

    @Test
    fun cardCancelButtonSendsCancelVisit() {
        setUi(VisitLogFakes.state())
        composeRule.onAllNodesWithText(text(R.string.cancel_visit))[0].performClick()
        assertTrue(events.last() is VisitLogEvents.CancelVisit)
    }

    @Test
    fun confirmIsDisabledUntilThereIsAReason() {
        setUi(sheetState())
        composeRule.onNodeWithText(text(R.string.cancel_visit_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.cancel_visit_confirm)).assertIsNotEnabled()
        composeRule.runOnIdle { reason.setTextAndPlaceCursorAtEnd("The doctor is away") }
        composeRule.onNodeWithText(text(R.string.cancel_visit_confirm)).assertIsEnabled().performClick()
        assertEquals(VisitLogEvents.ConfirmCancel, events.last())
    }

    @Test
    fun goBackSendsDismiss() {
        setUi(sheetState())
        composeRule.onNodeWithText(text(R.string.cancel_visit_back)).performClick()
        assertEquals(VisitLogEvents.DismissOverlay, events.last())
    }

    @Test
    fun typingInTheNotesFillsTheReason() {
        setUi(sheetState())
        composeRule.onNodeWithText(text(R.string.cancel_visit_notes_hint)).performTextInput("away")
        assertEquals("away", reason.text.toString())
    }
}
