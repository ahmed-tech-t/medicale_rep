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
 * Given the card idle / recording / recorded. Tapping to record needs a real microphone and the
 * runtime permission, so that path is verified manually.
 */
class VoiceNoteCardTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun setCard(
        isRecording: Boolean,
        voiceNote: ByteArray? = null,
        onDelete: () -> Unit = {},
    ) {
        composeRule.setContent {
            MedicalRepTheme {
                VoiceNoteCard(
                    isRecording = isRecording,
                    voiceNote = voiceNote,
                    onRecordingStarted = {},
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
    fun recorded_showsNoteAndDeleteCallsBack() {
        var deleted = 0
        setCard(isRecording = false, voiceNote = ByteArray(1), onDelete = { deleted++ })
        composeRule.onNodeWithTag(VoiceNoteTags.Recorded).assertIsDisplayed()
        composeRule.onNodeWithTag(VoiceNoteTags.Play).assertIsDisplayed()
        composeRule.onNodeWithTag(VoiceNoteTags.RecordButton).assertDoesNotExist()
        composeRule.onNodeWithTag(VoiceNoteTags.Delete).performClick()
        assertEquals(1, deleted)
    }
}
