package com.example.medicalrep.ui.screens.planvisit.main

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
import com.example.medicalrep.ui.screens.planvisit.components.PlanVisitTestTags
import com.example.medicalrep.ui.screens.planvisit.components.PlanVisitUi
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
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
class PlanVisitUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<PlanVisitEvents>()

    private fun text(@StringRes id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    private fun setUi(state: PlanVisitState) {
        composeRule.setContent {
            MedicalRepTheme {
                PlanVisitUi(state = state, onEvent = { events += it })
            }
        }
    }

    @Test
    fun entityStep_showsNextOnly_andSendsNavigationEvents() {
        setUi(PlanVisitFakes.state())
        composeRule.onNodeWithTag(PlanVisitTestTags.Previous).assertDoesNotExist()
        composeRule.onNodeWithTag(PlanVisitTestTags.EndVisit).assertDoesNotExist()
        composeRule.onNodeWithTag(PlanVisitTestTags.Next).assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription(text(R.string.cd_back)).performClick()
        assertEquals(listOf(PlanVisitEvents.Next, PlanVisitEvents.Back), events)
    }

    @Test
    fun giftsStep_stepperAndAddPillSendEvents() {
        setUi(PlanVisitFakes.state().copy(currentStep = PlanVisitStep.GiftsProducts))
        composeRule.onAllNodesWithContentDescription(text(R.string.cd_increase_quantity))[0].performClick()
        composeRule.onNodeWithText(text(R.string.unplanned_add_format, text(R.string.unplanned_add_gift)))
            .performClick()
        composeRule.onNodeWithTag(PlanVisitTestTags.Previous).performClick()
        assertEquals(
            listOf(
                PlanVisitEvents.ChangeQuantity(ProductType.Gift, "gift-mug", 1),
                PlanVisitEvents.OpenAddItem(ProductType.Gift),
                PlanVisitEvents.Previous,
            ),
            events,
        )
    }

    @Test
    fun resultStep_endVisitAndClinicToggle() {
        setUi(PlanVisitFakes.state().copy(currentStep = PlanVisitStep.Result))
        val clinicOpen = text(R.string.unplanned_clinic_open)
        composeRule.onNodeWithTag(PlanVisitTestTags.Next).assertDoesNotExist()
        composeRule.onNodeWithTag(PlanVisitTestTags.Content).performScrollToNode(hasText(clinicOpen))
        composeRule.onNodeWithText(clinicOpen).performClick()
        composeRule.onNodeWithTag(PlanVisitTestTags.EndVisit).performClick()
        assertEquals(
            listOf(PlanVisitEvents.ToggleClinicOpen(false), PlanVisitEvents.EndVisit),
            events,
        )
    }

    @Test
    fun addItemOverlay_showsSheetWithSaveDisabled() {
        setUi(
            PlanVisitFakes.state().copy(
                currentStep = PlanVisitStep.GiftsProducts,
                overlay = PlanVisitOverlay.AddItem(ProductType.Gift),
            ),
        )
        composeRule.onNodeWithTag(PlanVisitTestTags.AddItemSheet).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.unplanned_sheet_save)).assertIsNotEnabled()
        composeRule.onNodeWithContentDescription(text(R.string.cd_close)).performClick()
        assertEquals(listOf(PlanVisitEvents.DismissOverlay), events)
    }
}
