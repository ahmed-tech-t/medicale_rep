package com.example.medicalrep.ui.screens.unplannedvisit

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.medicalrep.MainActivity
import com.example.medicalrep.ui.screens.home.main.HomeTestTags
import com.example.medicalrep.ui.screens.unplannedvisit.components.UnplannedVisitTestTags
import org.junit.Rule
import org.junit.Test

/**
 * E2E (critical path), language-independent via test tags:
 * Given Home, When the add-visit FAB is tapped, Then the unplanned visit opens on the entity step;
 * When Next, Next, End visit, Then the app returns to Home.
 */
@OptIn(ExperimentalTestApi::class)
class UnplannedVisitFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val timeoutMs = 10_000L

    @Test
    fun homeFab_throughAllSteps_endVisit_returnsHome() {
        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.onNodeWithTag(HomeTestTags.AddVisit).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(UnplannedVisitTestTags.Next), timeoutMs)
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Next).performClick()
        composeRule.waitUntilExactlyOneExists(hasTestTag(UnplannedVisitTestTags.Previous), timeoutMs)
        composeRule.onNodeWithTag(UnplannedVisitTestTags.Next).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(UnplannedVisitTestTags.EndVisit), timeoutMs)
        composeRule.onNodeWithTag(UnplannedVisitTestTags.EndVisit).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.waitUntilDoesNotExist(hasTestTag(UnplannedVisitTestTags.EndVisit), timeoutMs)
    }
}
