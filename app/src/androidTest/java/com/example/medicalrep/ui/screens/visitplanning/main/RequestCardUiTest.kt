package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitplanning.components.VisitPlanningUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Pending tab request cards (Figma 591:2917): waiting shows its status and note; approved shows its status and note;
 * rejected shows the reason and the "pick another time" button, which sends PickAnotherTime for that request.
 */
class RequestCardUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<VisitPlanningEvents>()

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setUi(vararg requests: VisitUi) {
        composeRule.setContent {
            MedicalRepTheme {
                VisitPlanningUi(
                    state = VisitPlanningFakes.state().copy(selectedTab = VisitPlanningTab.Pending),
                    visits = pagingItemsOf(requests.toList()),
                    onEvent = { events += it },
                )
            }
        }
    }

    private fun request(approval: ApprovalStatus) =
        VisitPlanningFakes.pendingVisits().first().copy(approval = approval, rejectionReason = "Too many visits")

    @Test
    fun waitingShowsItsNoteAndNoRejectionPart() {
        setUi(request(ApprovalStatus.Pending))
        composeRule.onNodeWithText(text(R.string.request_status_pending)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.request_note_pending)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.request_pick_another_time)).assertDoesNotExist()
    }

    @Test
    fun approvedShowsItsNote() {
        setUi(request(ApprovalStatus.Approved))
        composeRule.onNodeWithText(text(R.string.request_status_approved)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.request_note_approved)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.request_pick_another_time)).assertDoesNotExist()
    }

    @Test
    fun rejectedShowsTheReasonAndPickAnotherTimeSendsItsId() {
        val rejected = request(ApprovalStatus.Rejected)
        setUi(rejected)
        composeRule.onNodeWithText(text(R.string.request_status_rejected)).assertIsDisplayed()
        composeRule.onNodeWithText("Too many visits", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.request_pick_another_time)).performClick()
        assertEquals(VisitPlanningEvents.PickAnotherTime(rejected), events.last())
    }
}
