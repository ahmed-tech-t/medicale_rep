package com.example.medicalrep.ui.components.widgets

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Each dialog shows its title and buttons; every button calls its own action once. */
class VisitDraftDialogsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val calls = mutableListOf<String>()

    private fun text(id: Int) = composeRule.activity.getString(id)

    @Test
    fun saveDraftDialogHasThreeActions() {
        composeRule.setContent { MedicalRepTheme { SaveDraftDialog({ calls += "save" }, { calls += "discard" }, { calls += "cancel" }) } }
        composeRule.onNodeWithText(text(R.string.draft_save_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.draft_save)).performClick()
        composeRule.onNodeWithText(text(R.string.draft_dont_save)).performClick()
        composeRule.onNodeWithText(text(R.string.draft_cancel)).performClick()
        assertEquals(listOf("save", "discard", "cancel"), calls)
    }

    @Test
    fun pendingVisitDialogHasConfirmAndCancel() {
        composeRule.setContent { MedicalRepTheme { PendingVisitDialog({ calls += "confirm" }, { calls += "cancel" }) } }
        composeRule.onNodeWithText(text(R.string.pending_visit_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.pending_visit_cancel_continue)).performClick()
        composeRule.onNodeWithText(text(R.string.draft_cancel)).performClick()
        assertEquals(listOf("confirm", "cancel"), calls)
    }

    @Test
    fun discardPendingDialogHasConfirmAndKeep() {
        composeRule.setContent { MedicalRepTheme { DiscardPendingDialog({ calls += "confirm" }, { calls += "keep" }) } }
        composeRule.onNodeWithText(text(R.string.discard_pending_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.discard_pending_confirm)).performClick()
        composeRule.onNodeWithText(text(R.string.discard_pending_keep)).performClick()
        assertEquals(listOf("confirm", "keep"), calls)
    }
}
