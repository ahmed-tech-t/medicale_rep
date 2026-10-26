package com.example.medicalrep.ui.screens.planvisit

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.example.medicalrep.MainActivity
import com.example.medicalrep.ui.screens.home.main.HomeTestTags
import com.example.medicalrep.ui.screens.planvisit.components.PlanVisitTestTags
import org.junit.Rule
import org.junit.Test

/**
 * E2E (critical path), language-independent via test tags:
 * Given Home, When the add-visit FAB is tapped, Then the new visit opens on the entity step with Next disabled
 * (nothing is chosen yet); When back is pressed with nothing changed, Then the app returns to Home without a dialog.
 */
@OptIn(ExperimentalTestApi::class)
class PlanVisitFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val timeoutMs = 10_000L

    @Test
    fun homeFab_opensEmptyVisit_nextIsBlocked_backReturnsHome() {
        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.onNodeWithTag(HomeTestTags.AddVisit).performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag(PlanVisitTestTags.Next), timeoutMs)
        composeRule.onNodeWithTag(PlanVisitTestTags.Next).assertIsNotEnabled()

        Espresso.pressBack()
        composeRule.waitUntilExactlyOneExists(hasTestTag(HomeTestTags.AddVisit), timeoutMs)
        composeRule.waitUntilDoesNotExist(hasTestTag(PlanVisitTestTags.Next), timeoutMs)
    }
}
