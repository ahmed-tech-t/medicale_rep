package com.example.medicalrep.ui.screens.planvisit.components.result

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Given the card idle / recording / tapped. Long-press recording needs a real microphone and the
 * runtime permission, so that path is verified manually.
 */
class VoiceNoteCardTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun setCard(
        isRecording: Boolean,
        hasRecording: Boolean = false,
        onStarted: () -> Unit = {},
        onDelete: () -> Unit = {},
    ) {
        composeRule.setContent {
            MedicalRepTheme {
                VoiceNoteCard(
                    isRecording = isRecording,
                    hasRecording = hasRecording,
                    onRecordingStarted = onStarted,
                    onRecorded = {},
                    onPermissionDenied = {},
                    onDelete = onDelete,
                )
            }
        }
    }

    @Test
    fun idle_hidesWaveform() {
        setCard(isRecording = false)
        composeRule.onNodeWithTag(VoiceNoteTags.Waveform).assertDoesNotExist()
    }

    @Test
    fun recording_showsWaveform() {
        setCard(isRecording = true)
        composeRule.onNodeWithTag(VoiceNoteTags.Waveform).assertIsDisplayed()
    }

    @Test
    fun shortTap_doesNotStartRecording() {
        var started = 0
        setCard(isRecording = false, onStarted = { started++ })
        composeRule.onNodeWithTag(VoiceNoteTags.RecordButton).performClick()
        composeRule.waitForIdle()
        assertEquals(0, started)
    }

    @Test
    fun recorded_showsNoteAndDeleteCallsBack() {
        var deleted = 0
        setCard(isRecording = false, hasRecording = true, onDelete = { deleted++ })
        composeRule.onNodeWithTag(VoiceNoteTags.Recorded).assertIsDisplayed()
        composeRule.onNodeWithTag(VoiceNoteTags.RecordButton).assertDoesNotExist()
        composeRule.onNodeWithTag(VoiceNoteTags.Delete).performClick()
        assertEquals(1, deleted)
    }
}
