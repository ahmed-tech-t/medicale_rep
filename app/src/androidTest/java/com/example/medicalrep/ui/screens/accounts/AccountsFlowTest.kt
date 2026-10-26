package com.example.medicalrep.ui.screens.accounts

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.screens.accounts.components.AccountsUi
import com.example.medicalrep.ui.screens.accounts.main.AccountsViewModel
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

/** Critical path: open the filter, choose Hospitals, and only hospitals remain; choose All and everything returns. */
class AccountsFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun text(id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    @Test
    fun filterByHospitalsThenAll() {
        val viewModel = AccountsViewModel(FakeSettingsRepo)
        composeRule.setContent {
            MedicalRepTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                AccountsUi(state = state, onEvent = viewModel::onEvent)
            }
        }
        val all = AccountsFakes.accounts()
        val hospitals = all.filter { it.type == AccountType.Hospital }

        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        composeRule.onNodeWithText(text(R.string.accounts_filter_hospitals)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, hospitals.size)).assertIsDisplayed()
        composeRule.onNodeWithText(hospitals.first().name).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        composeRule.onNodeWithText(text(R.string.accounts_filter_all)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, all.size)).assertIsDisplayed()
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
