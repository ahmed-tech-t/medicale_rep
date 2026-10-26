package com.example.medicalrep.ui.screens.planvisit.components.result

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.utils.voice_recorder.VoiceRecorder
import com.appcore.core.ui.utils.voice_recorder.hasRecordAudioPermission
import com.appcore.core.ui.utils.voice_recorder.rememberRecordAudioPermissionLauncher
import com.appcore.core.ui.utils.voice_recorder.rememberVoicePlayer
import com.appcore.core.ui.utils.voice_recorder.rememberVoiceRecorder
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.ErrorContainer
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate800
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

object VoiceNoteTags {
    const val RecordButton = "voice_note_record_button"
    const val Waveform = "voice_note_waveform"
    const val Recorded = "voice_note_recorded"
    const val Play = "voice_note_play"
    const val Delete = "voice_note_delete"
}

/** The recorded note: play/pause, its length and delete. */
@Composable
private fun VoiceNotePlayer(voiceNote: ByteArray, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val player = rememberVoicePlayer(voiceNote)
    val title = stringResource(R.string.unplanned_voice_note)
    val seconds = player.durationMillis / 1000
    Row(
        modifier = modifier
            .padding(vertical = spacing.spaceSmall)
            .testTag(VoiceNoteTags.Recorded),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundButton(
            container = MaterialTheme.colorScheme.primary,
            onClick = player.toggle,
            modifier = Modifier.testTag(VoiceNoteTags.Play),
        ) {
            if (player.isPlaying) {
                NexoraIcon(painter = painterResource(R.drawable.ic_pause), contentDescription = title, tint = Color.White)
            } else {
                NexoraIcon(imageVector = Icons.Filled.PlayArrow, contentDescription = title, tint = Color.White)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            NexoraText(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate800,
            )
            NexoraText(
                text = "%d:%02d".format(seconds / 60, seconds % 60),
                style = MaterialTheme.typography.labelSmall,
                color = Slate500,
            )
        }
        RoundButton(
            container = ErrorContainer,
            onClick = onDelete,
            modifier = Modifier.testTag(VoiceNoteTags.Delete),
        ) {
            NexoraIcon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.cd_delete_voice_note),
                tint = Error,
            )
        }
    }
}

/** A 40 dp circle that is exactly its size (no extra touch padding), so it fits inside the note row. */
@Composable
private fun RoundButton(
    container: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(container)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/** Tap to record (up to 2 minutes), tap again to stop; the clip is delivered through [onRecorded] and can be played back. */
@Composable
internal fun VoiceNoteCard(
    isRecording: Boolean,
    voiceNote: ByteArray?,
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

    // The 2 minute limit: a tap normally stops the recording before this.
    LaunchedEffect(isRecording) {
        if (isRecording) {
            delay(VoiceRecorder.MAX_DURATION_MILLIS.milliseconds)
            currentOnRecorded(recorder.stop())
        }
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
            if (voiceNote != null && !isRecording) {
                VoiceNotePlayer(voiceNote = voiceNote, onDelete = onDelete, modifier = Modifier.weight(1f))
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
                onClick = {
                    if (isRecording) {
                        currentOnRecorded(recorder.stop())
                    } else if (!context.hasRecordAudioPermission()) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else if (recorder.start()) {
                        currentOnStarted()
                    }
                },
                fillMaxWidth = false,
                compact = true,
                modifier = Modifier.testTag(VoiceNoteTags.RecordButton),
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
            voiceNote = null,
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
            voiceNote = null,
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
            voiceNote = null,
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
            voiceNote = ByteArray(1),
            onRecordingStarted = {},
            onRecorded = {},
            onPermissionDenied = {},
            onDelete = {},
        )
    }
}
