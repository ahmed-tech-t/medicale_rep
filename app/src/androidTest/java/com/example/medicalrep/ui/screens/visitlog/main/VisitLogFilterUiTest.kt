package com.example.medicalrep.ui.screens.visitlog.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.widgets.filter.ClassFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterEvents
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitlog.components.VisitLogUi
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The filter icon sends OpenFilter; with the sheet open its sections show, a class chip sends the form event and Apply sends ApplyFilter. */
class VisitLogFilterUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<VisitLogEvents>()

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setUi(state: VisitLogState) {
        composeRule.setContent {
            MedicalRepTheme {
                VisitLogUi(state = state, visits = pagingItemsOf(VisitLogFakes.requiredVisits()), onEvent = { events += it })
            }
        }
    }

    @Test
    fun filterIconSendsOpenFilter() {
        setUi(VisitLogFakes.state())
        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        assertTrue(events.contains(VisitLogEvents.OpenFilter))
    }

    @Test
    fun requiredTabSheetListsClassRegionSpecialtyAndPeriod() {
        setUi(VisitLogFakes.state().copy(overlay = VisitLogOverlay.Filter))
        listOf(
            R.string.filter_class_title,
            R.string.filter_region_title,
            R.string.unplanned_label_specialty,
            R.string.filter_period,
        ).forEach { composeRule.onNodeWithText(text(it)).assertIsDisplayed() }
        composeRule.onNodeWithText("B").performClick()
        assertEquals(VisitLogEvents.OnFilterEvent(FilterEvents.OnClassSelect(ClassFilter.B)), events.last())
    }

    @Test
    fun doneTabSheetListsOnlySyncStatusVisitTypeAndPeriod() {
        setUi(VisitLogFakes.state().copy(selectedTab = VisitLogTab.Done, overlay = VisitLogOverlay.Filter))
        listOf(R.string.filter_done_visits_title, R.string.filter_sync_status_title, R.string.visit_type_label, R.string.filter_period).forEach {
            composeRule.onNodeWithText(text(it)).assertIsDisplayed()
        }
        listOf(R.string.filter_class_title, R.string.filter_region_title, R.string.unplanned_label_specialty).forEach {
            composeRule.onNodeWithText(text(it)).assertDoesNotExist()
        }
    }

    @Test
    fun typingInTheSearchBoxSendsOnSearchChange() {
        setUi(VisitLogFakes.state())
        composeRule.onNodeWithText(text(R.string.visit_log_search_hint)).performTextInput("Mona")
        composeRule.waitForIdle()
        assertEquals(VisitLogEvents.OnSearchChange("Mona"), events.last())
    }

    @Test
    fun applySendsApplyFilter() {
        setUi(VisitLogFakes.state().copy(overlay = VisitLogOverlay.Filter))
        composeRule.onNodeWithText(text(R.string.filter_apply)).performClick()
        assertEquals(VisitLogEvents.ApplyFilter, events.last())
    }
}
