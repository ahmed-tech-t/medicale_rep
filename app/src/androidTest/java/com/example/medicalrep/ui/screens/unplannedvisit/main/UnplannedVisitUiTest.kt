package com.example.medicalrep.ui.screens.unplannedvisit.main

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.screens.unplannedvisit.components.UnplannedVisitTestTags
import com.example.medicalrep.ui.screens.unplannedvisit.components.UnplannedVisitUi
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * UI cases (strings resolved from resources, so they pass in any app language):
 * 1. Entity step shows Next only (no Previous / End visit); Next sends Next; back arrow sends Back.
 * 2. Gifts step: "+" on the gift row sends ChangeQuantity(Gift, +1); the add-gift pill sends
 *    OpenAddItem(Gift); Previous sends Previous.
 * 3. Result step shows End visit instead of Next; the clinic row toggles; End visit sends EndVisit.
 * 4. With the AddItem overlay, the sheet shows, Save is disabled until an item is picked, close dismisses.
 */
class UnplannedVisitUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<UnplannedVisitEvents>()

    private fun text(@StringRes id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    private fun setUi(state: UnplannedVisitState) {
        composeRule.setContent {
            MedicalRepTheme {
                UnplannedVisitUi(state = state, onEvent = { events += it })
            }
        }
    }

    @Test
    fun entityStep_showsNextOnly_andSendsNavigationEvents() {
        setUi(UnplannedVisitFakes.state())
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Previous).assertDoesNotExist()
        composeRule.onNodeWithTag(UnplannedVisitTestTags.EndVisit).assertDoesNotExist()
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Next).assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription(text(R.string.cd_back)).performClick()
        assertEquals(listOf(UnplannedVisitEvents.Next, UnplannedVisitEvents.Back), events)
    }

    @Test
    fun giftsStep_stepperAndAddPillSendEvents() {
        setUi(UnplannedVisitFakes.state().copy(currentStep = UnplannedVisitStep.GiftsProducts))
        composeRule.onAllNodesWithContentDescription(text(R.string.cd_increase_quantity))[0].performClick()
        composeRule.onNodeWithText(text(R.string.unplanned_add_format, text(R.string.unplanned_add_gift)))
            .performClick()
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Previous).performClick()
        assertEquals(
            listOf(
                UnplannedVisitEvents.ChangeQuantity(ProductType.Gift, "gift-mug", 1),
                UnplannedVisitEvents.OpenAddItem(ProductType.Gift),
                UnplannedVisitEvents.Previous,
            ),
            events,
        )
    }

    @Test
    fun resultStep_endVisitAndClinicToggle() {
        setUi(UnplannedVisitFakes.state().copy(currentStep = UnplannedVisitStep.Result))
        val clinicOpen = text(R.string.unplanned_clinic_open)
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Next).assertDoesNotExist()
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Content).performScrollToNode(hasText(clinicOpen))
        composeRule.onNodeWithText(clinicOpen).performClick()
        composeRule.onNodeWithTag(UnplannedVisitTestTags.EndVisit).performClick()
        assertEquals(
            listOf(UnplannedVisitEvents.ToggleClinicOpen(false), UnplannedVisitEvents.EndVisit),
            events,
        )
    }

    @Test
    fun addItemOverlay_showsSheetWithSaveDisabled() {
        setUi(
            UnplannedVisitFakes.state().copy(
                currentStep = UnplannedVisitStep.GiftsProducts,
                overlay = UnplannedVisitOverlay.AddItem(ProductType.Gift),
            ),
        )
        composeRule.onNodeWithTag(UnplannedVisitTestTags.AddItemSheet).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.unplanned_sheet_save)).assertIsNotEnabled()
        composeRule.onNodeWithContentDescription(text(R.string.cd_close)).performClick()
        assertEquals(listOf(UnplannedVisitEvents.DismissOverlay), events)
    }
}
