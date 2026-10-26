package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.screens.visitplanning.components.VisitPlanningUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The filter icon sends OpenFilter; with the popup open its 6 own options show and tapping one sends SelectFilter. */
class VisitPlanningFilterUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<VisitPlanningEvents>()

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setUi(state: VisitPlanningState) {
        composeRule.setContent { MedicalRepTheme { VisitPlanningUi(state = state, onEvent = { events += it }) } }
    }

    @Test
    fun filterIconSendsOpenFilter() {
        setUi(VisitPlanningFakes.state())
        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        assertTrue(events.contains(VisitPlanningEvents.OpenFilter))
    }

    @Test
    fun popupListsItsOwnOptionsAndSendsTheChosenFilter() {
        setUi(VisitPlanningFakes.state().copy(overlay = VisitPlanningOverlay.Filter))
        listOf(
            R.string.filter_all,
            R.string.filter_morning,
            R.string.filter_evening,
            R.string.filter_class_a,
            R.string.filter_class_b,
            R.string.filter_class_c,
        ).forEach { composeRule.onNodeWithText(text(it)).assertIsDisplayed() }
        composeRule.onNodeWithText(text(R.string.filter_class_b)).performClick()
        assertEquals(VisitPlanningEvents.SelectFilter(VisitFilter.ClassB), events.last())
    }
}
