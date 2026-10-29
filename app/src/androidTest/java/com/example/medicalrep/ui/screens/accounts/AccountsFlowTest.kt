package com.example.medicalrep.ui.screens.accounts

import com.example.medicalrep.domain.models.BasicModel
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.R
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.repo.AccountRepoImpl
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.ui.screens.accounts.components.AccountsUi
import com.example.medicalrep.ui.screens.accounts.main.AccountsViewModel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Critical path on a real in-memory Room: open the filter, choose Hospitals, only hospitals remain; All brings everything back. */
class AccountsFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java).build()
        runBlocking {
            val repo = AccountRepoImpl(db.accountDao())
            repo.save(Account("a1", name = "عيادة د. محمود", accountType = BasicModel("عيادة", "عيادة")))
            repo.save(Account("a2", name = "عيادة د. هاني", accountType = BasicModel("عيادة", "عيادة")))
            repo.save(Account("a3", name = "مستشفى الدلتا", accountType = BasicModel("مستشفى", "مستشفى")))
        }
    }

    @After
    fun tearDown() = db.close()

    private fun text(id: Int, vararg args: Any) = composeRule.activity.getString(id, *args)

    @Test
    fun filterByHospitalsThenAll() {
        val viewModel = AccountsViewModel(FakeSettingsRepo, AccountRepoImpl(db.accountDao()))
        composeRule.setContent {
            MedicalRepTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                AccountsUi(state = state, accounts = viewModel.accounts.collectAsLazyPagingItems(), onEvent = viewModel::onEvent)
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, 3)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        composeRule.onNodeWithText(text(R.string.accounts_filter_hospitals)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, 1)).assertIsDisplayed()
        composeRule.onNodeWithText("مستشفى الدلتا").assertIsDisplayed()

        composeRule.onNodeWithContentDescription(text(R.string.cd_filter)).performClick()
        composeRule.onNodeWithText(text(R.string.filter_all)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text(R.string.visit_log_results_format, 3)).assertIsDisplayed()
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
