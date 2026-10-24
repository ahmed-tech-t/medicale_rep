package com.example.medicalrep.ui.screens.planvisit.components.result

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.utils.voice_recorder.VoiceRecorder
import com.appcore.core.ui.utils.voice_recorder.hasRecordAudioPermission
import com.appcore.core.ui.utils.voice_recorder.rememberRecordAudioPermissionLauncher
import com.appcore.core.ui.utils.voice_recorder.rememberVoiceRecorder
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Success
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate800
import kotlinx.coroutines.withTimeoutOrNull

object VoiceNoteTags {
    const val RecordButton = "voice_note_record_button"
    const val Waveform = "voice_note_waveform"
    const val Recorded = "voice_note_recorded"
    const val Delete = "voice_note_delete"
}

@Composable
private fun RecordedNote(onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.testTag(VoiceNoteTags.Recorded),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraIcon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Success,
            modifier = Modifier.size(dim.smallIconSize),
        )
        NexoraText(
            text = stringResource(R.string.unplanned_voice_recorded),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate600,
            modifier = Modifier.weight(1f),
        )
        NexoraIconButton(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cd_delete_voice_note),
            onClick = onDelete,
            tint = Slate600,
            modifier = Modifier.testTag(VoiceNoteTags.Delete),
        )
    }
}

/** Hold the button to record (up to 2 minutes); releasing stops and delivers the clip through [onRecorded]. */
@Composable
internal fun VoiceNoteCard(
    isRecording: Boolean,
    hasRecording: Boolean,
    onRecordingStarted: () -> Unit,
    onRecorded: (ByteArray?) -> Unit,
    onPermissionDenied: () -> Unit,
    onDelete: () -> Unit,
) {
    val recorder = rememberVoiceRecorder()
    val context = LocalContext.current
    val currentOnStarted by rememberUpdatedState(onRecordingStarted)
    val currentOnRecorded by rememberUpdatedState(onRecorded)
    val permissionLauncher = rememberRecordAudioPermissionLauncher { granted ->
        if (!granted) onPermissionDenied()
    }

    StepCard {
        NexoraText(
            text = stringResource(R.string.unplanned_notes_label),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate800,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radii.medium))
                .background(Slate25)
                .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.medium))
                .padding(horizontal = spacing.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (hasRecording && !isRecording) {
                RecordedNote(onDelete = onDelete, modifier = Modifier.weight(1f))
                return@Row
            }
            if (isRecording) {
                AnimatedMockWaveform(
                    modifier = Modifier
                        .weight(1f)
                        .height(dim.iconSize)
                        .testTag(VoiceNoteTags.Waveform),
                )
            } else {
                NexoraText(
                    text = stringResource(R.string.unplanned_voice_prompt),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate600,
                    modifier = Modifier.weight(1f),
                )
            }
            NexoraButton(
                text = stringResource(
                    if (isRecording) R.string.unplanned_voice_recording else R.string.unplanned_voice_note,
                ),
                onClick = {},
                fillMaxWidth = false,
                compact = true,
                modifier = Modifier
                    .testTag(VoiceNoteTags.RecordButton)
                    .pointerInput(recorder) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            val releasedEarly = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                waitForUpOrCancellation(PointerEventPass.Initial)
                                true
                            } != null
                            if (releasedEarly) return@awaitEachGesture

                            if (!context.hasRecordAudioPermission()) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@awaitEachGesture
                            }
                            if (!recorder.start()) return@awaitEachGesture
                            currentOnStarted()
                            withTimeoutOrNull(VoiceRecorder.MAX_DURATION_MILLIS) {
                                waitForUpOrCancellation(PointerEventPass.Initial)
                            }
                            currentOnRecorded(recorder.stop())
                        }
                    },
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VoiceNoteCardArabicPreview() {
    MedicalRepTheme {
        VoiceNoteCard(
            isRecording = false,
            hasRecording = false,
            onRecordingStarted = {},
            onRecorded = {},
            onPermissionDenied = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceNoteCardEnglishPreview() {
    MedicalRepTheme {
        VoiceNoteCard(
            isRecording = false,
            hasRecording = false,
            onRecordingStarted = {},
            onRecorded = {},
            onPermissionDenied = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceNoteCardRecordingPreview() {
    MedicalRepTheme {
        VoiceNoteCard(
            isRecording = true,
            hasRecording = false,
            onRecordingStarted = {},
            onRecorded = {},
            onPermissionDenied = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VoiceNoteCardRecordedPreview() {
    MedicalRepTheme {
        VoiceNoteCard(
            isRecording = false,
            hasRecording = true,
            onRecordingStarted = {},
            onRecorded = {},
            onPermissionDenied = {},
            onDelete = {},
        )
    }
}
