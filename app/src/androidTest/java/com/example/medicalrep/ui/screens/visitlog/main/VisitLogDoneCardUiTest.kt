package com.example.medicalrep.ui.screens.visitlog.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.components.widgets.visit.CompletedVisitCard
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** A done card starts collapsed; tapping it shows the details, the upload note only while not synced, and the report link. */
class VisitLogDoneCardUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var reports = 0

    private fun text(id: Int) = composeRule.activity.getString(id)

    private fun setCard(visit: VisitUi) {
        composeRule.setContent { MedicalRepTheme { CompletedVisitCard(model = visit, onViewReport = { reports++ }) } }
    }

    private val notSynced get() = VisitSamples.planned().toUi().copy(id = "v1", syncStatus = SyncStatus.NotSynced)
    private val failed get() = notSynced.copy(syncStatus = SyncStatus.Failed)

    @Test
    fun startsCollapsedWithTheSyncPillOnly() {
        setCard(notSynced)
        composeRule.onNodeWithText(text(R.string.visit_log_not_synced)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.visit_log_view_report)).assertDoesNotExist()
    }

    @Test
    fun tapShowsTheReportLinkAndNoUploadNoteForAWaitingVisit() {
        setCard(notSynced)
        composeRule.onNodeWithText(text(R.string.visit_log_not_synced)).performClick()
        composeRule.onNodeWithText(text(R.string.visit_log_view_report)).assertIsDisplayed().performClick()
        assertEquals(1, reports)
        composeRule.onNodeWithText(text(R.string.visit_log_sync_failed_body), substring = true).assertDoesNotExist()
    }

    @Test
    fun aFailedVisitShowsTheFailedPillAndTheUploadNote() {
        setCard(failed)
        composeRule.onNodeWithText(text(R.string.visit_log_not_synced)).performClick()
        composeRule.onNodeWithText(text(R.string.visit_log_sync_failed_body), substring = true).assertIsDisplayed()
    }

    @Test
    fun aSyncedVisitHasNoUploadNote() {
        setCard(notSynced.copy(syncStatus = SyncStatus.Synced, backendId = "432423"))
        composeRule.onNodeWithText(text(R.string.filter_synced_done)).performClick()
        composeRule.onNodeWithText(text(R.string.visit_log_view_report)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.visit_log_sync_failed_body), substring = true).assertDoesNotExist()
    }
}
