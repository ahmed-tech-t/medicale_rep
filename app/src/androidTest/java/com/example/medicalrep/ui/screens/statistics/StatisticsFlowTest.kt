package com.example.medicalrep.ui.screens.statistics

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.data.local.dao.visit
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.repo.StatisticsRepoImpl
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.ui.screens.statistics.components.StatisticsUi
import com.example.medicalrep.ui.screens.statistics.main.StatisticsViewModel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Critical path on a real in-memory Room: this week's visits show as done out of planned, and a visit done later raises the count. */
class StatisticsFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java).build()
        runBlocking {
            db.accountDao().save(Account("a1", name = "عيادة د. محمود", entityType = "clinic", specialty = "Cardio", classCategory = "A").toEntity())
            val now = System.currentTimeMillis()
            db.visitDao().save(visit("done", accountId = "a1", status = VisitStatus.Completed, dateTimeMillis = now, completedAtMillis = now), emptyList())
            db.visitDao().save(visit("todo", accountId = "a1", status = VisitStatus.Scheduled, dateTimeMillis = now), emptyList())
        }
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun showsTheWeeksVisitsAndFollowsTheData() {
        val viewModel = StatisticsViewModel(FakeSettingsRepo, StatisticsRepoImpl(db.statisticsDao(), db.custodyDao()))
        composeRule.setContent {
            MedicalRepTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                StatisticsUi(state = state, onEvent = viewModel::onEvent)
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("1 / 2").assertIsDisplayed()

        runBlocking {
            val now = System.currentTimeMillis()
            db.visitDao().save(visit("todo", accountId = "a1", status = VisitStatus.Completed, dateTimeMillis = now, completedAtMillis = now), emptyList())
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("2 / 2").assertIsDisplayed()
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
