package com.example.medicalrep.ui.screens.statistics.main

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.statistics.components.StatisticsUi
import com.example.medicalrep.ui.screens.statistics.utils.StatisticsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * 1. The title, the week's section, the completed visits ("10 / 26") and the three section names are shown.
 * 2. The stock cards show their titles and the class rows their percents (50%, 33%, 17%).
 * 3. The back arrow sends Back.
 * 4. With zeros (nothing loaded yet) the page still shows "0 / 0" and every section.
 */
class StatisticsUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<StatisticsEvents>()

    private fun text(@StringRes id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    private fun setUi(state: StatisticsState) {
        composeRule.setContent { MedicalRepTheme { StatisticsUi(state = state, onEvent = { events += it }) } }
    }

    @Test
    fun showsTheTitleTheWeeksVisitsAndTheSections() {
        setUi(StatisticsFakes.state())
        composeRule.onNodeWithText(text(R.string.nav_statics)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.statistics_section_week_visits)).assertIsDisplayed()
        composeRule.onNodeWithText("10 / 26").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.statistics_completed_visits)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.statistics_section_stock)).assertExists()
        composeRule.onNodeWithText(text(R.string.statistics_section_classes)).assertExists()
    }

    @Test
    fun showsTheStockCardsAndTheClassPercents() {
        setUi(StatisticsFakes.state())
        listOf(R.string.statistics_samples, R.string.statistics_gifts, R.string.statistics_products)
            .forEach { composeRule.onNodeWithText(text(it)).assertExists() }
        listOf("50%", "33%", "17%").forEach { composeRule.onNodeWithText(it).assertExists() }
    }

    @Test
    fun backArrowSendsBack() {
        setUi(StatisticsFakes.state())
        composeRule.onNodeWithContentDescription(text(R.string.cd_back)).performClick()
        assertEquals(listOf<StatisticsEvents>(StatisticsEvents.Back), events)
    }

    @Test
    fun emptyStatisticsStillShowEverySection() {
        setUi(StatisticsState())
        composeRule.onNodeWithText("0 / 0").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.statistics_section_stock)).assertExists()
        composeRule.onNodeWithText(text(R.string.statistics_section_classes)).assertExists()
    }
}
