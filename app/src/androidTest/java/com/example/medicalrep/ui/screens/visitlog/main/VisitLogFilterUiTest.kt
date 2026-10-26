package com.example.medicalrep.ui.screens.visitlog.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitlog.components.VisitLogUi
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The filter icon sends OpenFilter; with the popup open its 5 own options show and tapping one sends SelectFilter. */
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
    fun requiredTabPopupListsShiftAndClassOptions() {
        setUi(VisitLogFakes.state().copy(overlay = VisitLogOverlay.Filter))
        listOf(
            R.string.filter_all,
            R.string.filter_morning,
            R.string.filter_evening,
            R.string.filter_class_a,
            R.string.filter_class_b,
            R.string.filter_class_c,
        ).forEach { composeRule.onNodeWithText(text(it)).assertIsDisplayed() }
        composeRule.onNodeWithText(text(R.string.filter_class_a)).performClick()
        assertEquals(VisitLogEvents.SelectFilter(VisitFilter.ClassA), events.last())
    }

    @Test
    fun doneTabPopupListsDoubleAndSyncOptions() {
        setUi(VisitLogFakes.state().copy(selectedTab = VisitLogTab.Done, overlay = VisitLogOverlay.Filter))
        listOf(
            R.string.filter_all,
            R.string.filter_morning,
            R.string.filter_evening,
            R.string.filter_double_visits,
            R.string.filter_synced,
            R.string.filter_not_synced,
        ).forEach { composeRule.onNodeWithText(text(it)).assertIsDisplayed() }
        composeRule.onNodeWithText(text(R.string.filter_synced)).performClick()
        assertEquals(VisitLogEvents.SelectFilter(VisitFilter.Synced), events.last())
    }
}
