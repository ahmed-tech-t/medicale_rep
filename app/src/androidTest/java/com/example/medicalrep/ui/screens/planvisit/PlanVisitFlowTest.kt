package com.example.medicalrep.ui.screens.planvisit

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.medicalrep.MainActivity
import com.example.medicalrep.ui.screens.home.main.HomeTestTags
import com.example.medicalrep.ui.screens.planvisit.components.PlanVisitTestTags
import org.junit.Rule
import org.junit.Test

/**
 * E2E (critical path), language-independent via test tags:
 * Given Home, When the add-visit FAB is tapped, Then the unplanned visit opens on the entity step;
 * When Next, Next, End visit, Then the app returns to Home.
 */
@OptIn(ExperimentalTestApi::class)
class PlanVisitFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val timeoutMs = 10_000L

    @Test
    fun homeFab_throughAllSteps_endVisit_returnsHome() {
        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.onNodeWithTag(HomeTestTags.AddVisit).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(PlanVisitTestTags.Next), timeoutMs)
        composeRule.onNodeWithTag(PlanVisitTestTags.Next).performClick()
        composeRule.waitUntilExactlyOneExists(hasTestTag(PlanVisitTestTags.Previous), timeoutMs)
        composeRule.onNodeWithTag(PlanVisitTestTags.Next).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(PlanVisitTestTags.EndVisit), timeoutMs)
        composeRule.onNodeWithTag(PlanVisitTestTags.EndVisit).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.waitUntilDoesNotExist(hasTestTag(PlanVisitTestTags.EndVisit), timeoutMs)
    }
}
