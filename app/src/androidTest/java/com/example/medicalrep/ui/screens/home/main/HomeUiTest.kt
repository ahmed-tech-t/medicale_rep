package com.example.medicalrep.ui.screens.home.main

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.medicalrep.R
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyDescendant
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI cases (strings resolved from resources, so they pass in any app language):
 * 1. Home shows the header name, both today visits and the "Add visit" FAB; stats and custody are reachable by scroll.
 * 2. Tapping the FAB sends OnAddVisitClick.
 * 3. Opening the drawer state shows the drawer rows; tapping "Products" sends OnDrawerItem(Products).
 * 4. With no today visits, the list shows no visit cards.
 */
class HomeUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<HomeEvents>()

    private fun text(@StringRes id: Int) = composeRule.activity.getString(id)

    private fun setHome(state: HomeState = HomeSamples.homeState(language = "en")) {
        composeRule.setContent {
            MedicalRepTheme {
                HomeUi(state = state, onEvent = { events += it })
            }
        }
    }

    @Test
    fun home_showsDesignSections() {
        setHome()
        composeRule.onNodeWithTag(HomeTestTags.Header)
            .assertIsDisplayed()
            .assert(hasAnyDescendant(hasText("أحمد محمد")))
        composeRule.onAllNodesWithTag(HomeTestTags.TodayVisit).assertCountEquals(2)
        composeRule.onNodeWithTag(HomeTestTags.AddVisit).assertIsDisplayed()
        composeRule.onNodeWithTag(HomeTestTags.List).performScrollToNode(hasText(text(R.string.home_section_visit_stats)))
        composeRule.onNodeWithText(text(R.string.statics_metric_clinics)).assertIsDisplayed()
        composeRule.onNodeWithTag(HomeTestTags.List).performScrollToNode(hasText(text(R.string.statics_section_custody)))
    }

    @Test
    fun fab_sendsAddVisit() {
        setHome()
        composeRule.onNodeWithTag(HomeTestTags.AddVisit).performClick()
        assertEquals(HomeEvents.OnAddVisitClick, events.last())
    }

    @Test
    fun openDrawer_rowSendsItem() {
        setHome(HomeSamples.homeState(language = "en").copy(isDrawerOpen = true))
        composeRule.onNodeWithText(text(R.string.products)).assertIsDisplayed().performClick()
        assertTrue(events.contains(HomeEvents.OnDrawerItem(NavDrawerItem.Products)))
    }

    @Test
    fun emptyToday_showsNoVisitCards() {
        setHome(HomeSamples.homeState(language = "en").copy(todayVisits = emptyList(), activeVisit = null))
        composeRule.onAllNodesWithTag(HomeTestTags.TodayVisit).assertCountEquals(0)
    }
}
