package com.example.medicalrep.ui.screens.accounts.main

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.screens.accounts.components.AccountsUi
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 1. The header title, the result count and the first account are shown.
 * 2. With the popup open, all 5 options are listed; tapping one sends SelectType with its type; All sends null.
 * 3. The filter icon sends OpenFilter.
 * 4. With a type selected and only that type loaded, the list shows only that type and the count follows.
 */
class AccountsUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<AccountsEvents>()

    private fun text(@StringRes id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    private fun setUi(state: AccountsState, accounts: List<AccountUi> = AccountsFakes.accounts()) {
        composeRule.setContent {
            MedicalRepTheme {
                val items = flowOf(PagingData.from(accounts)).collectAsLazyPagingItems()
                AccountsUi(state = state, accounts = items, onEvent = { events += it })
            }
        }
    }

    @Test
    fun showsTitleCountAndFirstAccount() {
        val first = AccountsFakes.accounts().first()
        setUi(AccountsFakes.state())
        composeRule.onNodeWithText(text(R.string.accounts)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, 10)).assertIsDisplayed()
        composeRule.onNodeWithText(first.name).assertIsDisplayed()
    }

    @Test
    fun filterIconSendsOpenFilter() {
        setUi(AccountsFakes.state())
        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        assertTrue(events.contains(AccountsEvents.OpenFilter))
    }

    @Test
    fun popupListsFiveOptionsAndSendsTheChosenType() {
        setUi(AccountsFakes.state().copy(overlay = AccountsOverlay.TypeFilter))
        listOf(
            R.string.filter_all,
            R.string.accounts_filter_clinics,
            R.string.accounts_filter_hospitals,
            R.string.accounts_filter_pharmacies,
            R.string.accounts_filter_medical_centers,
        ).forEach { composeRule.onNodeWithText(text(it)).assertIsDisplayed() }
        composeRule.onNodeWithText(text(R.string.accounts_filter_hospitals)).performClick()
        assertEquals(AccountsEvents.SelectType(AccountType.Hospital), events.last())
        composeRule.onNodeWithText(text(R.string.filter_all)).performClick()
        assertEquals(AccountsEvents.SelectType(null), events.last())
    }

    @Test
    fun selectedTypeShowsOnlyThatType() {
        val hospitals = AccountsFakes.accounts().filter { it.type == AccountType.Hospital }
        setUi(AccountsFakes.state().copy(selectedType = AccountType.Hospital, resultCount = hospitals.size), hospitals)
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, hospitals.size)).assertIsDisplayed()
        composeRule.onNodeWithText(hospitals.first().name).assertIsDisplayed()
    }
}
