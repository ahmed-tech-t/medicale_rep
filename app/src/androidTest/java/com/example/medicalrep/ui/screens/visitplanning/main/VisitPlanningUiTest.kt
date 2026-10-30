package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitplanning.components.VisitPlanningUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Visits tab (Figma 591:3150): the filter button sends OpenFilter, typing sends OnSearchChange, tapping a day sends ToggleDay,
 * the schedule button shows only with a pick and sends ScheduleSelectedDays.
 * Pending tab (Figma 591:2917): the four chips show and tapping one sends SelectApproval.
 * Booked tile: tapping a booked day fires no event.
 */
class VisitPlanningUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<VisitPlanningEvents>()

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setUi(state: VisitPlanningState, visits: List<com.example.medicalrep.ui.models.VisitUi> = VisitPlanningFakes.visits()) {
        composeRule.setContent {
            MedicalRepTheme {
                VisitPlanningUi(state = state, visits = pagingItemsOf(visits), onEvent = { events += it })
            }
        }
    }

    @Test
    fun filterButtonSendsOpenFilter() {
        setUi(VisitPlanningFakes.state())
        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        assertTrue(events.contains(VisitPlanningEvents.OpenFilter))
    }

    @Test
    fun typingInTheSearchBoxSendsTheText() {
        setUi(VisitPlanningFakes.state())
        composeRule.onNodeWithText(text(R.string.visit_planning_search_hint)).performTextInput("mona")
        assertEquals(VisitPlanningEvents.OnSearchChange("mona"), events.last())
    }

    @Test
    fun tappingADaySendsItAndTheScheduleButtonNeedsAPick() {
        val state = VisitPlanningFakes.state()
        setUi(state)
        composeRule.onNodeWithText(text(R.string.visit_planning_schedule_action)).assertDoesNotExist()
        val visit = VisitPlanningFakes.visits().first()
        composeRule.onAllNodesWithText(state.days[1].date)[0].performClick()
        assertEquals(VisitPlanningEvents.ToggleDay(visit.id, state.days[1].millis), events.last())
    }

    @Test
    fun scheduleButtonShowsWithAPickAndSendsTheSchedule() {
        val state = VisitPlanningFakes.state()
        val visit = VisitPlanningFakes.visits().first()
        setUi(state.copy(selectedDays = mapOf(visit.id to setOf(state.days[1].millis))))
        composeRule.onNodeWithText(text(R.string.visit_planning_schedule_action)).assertIsDisplayed().performClick()
        assertEquals(VisitPlanningEvents.ScheduleSelectedDays, events.last())
    }

    @Test
    fun tappingABookedDayFiresNoEvent() {
        val state = VisitPlanningFakes.state()
        val visit = VisitPlanningFakes.visits().first()
        val bookedDay = state.days[0]
        setUi(state.copy(bookedDays = mapOf(visit.accountId to setOf(bookedDay.millis))))
        val before = events.size
        composeRule.onAllNodesWithText(bookedDay.date)[0].performClick()
        assertEquals(before, events.size)
    }

    @Test
    fun pendingTabShowsTheChipsAndSendsTheChosenAnswer() {
        setUi(VisitPlanningFakes.state().copy(selectedTab = VisitPlanningTab.Pending), VisitPlanningFakes.pendingVisits())
        listOf(R.string.filter_all, R.string.request_status_pending, R.string.request_status_approved, R.string.request_status_rejected)
            .forEach { composeRule.onAllNodesWithText(text(it))[0].assertIsDisplayed() }
        composeRule.onAllNodesWithText(text(R.string.request_status_rejected))[0].performClick()
        assertEquals(VisitPlanningEvents.SelectApproval(ApprovalStatus.Rejected), events.last())
    }
}
