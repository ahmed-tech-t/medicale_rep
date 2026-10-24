package com.example.medicalrep.ui.screens.planvisit.components.result

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.utils.document_picker.createCameraImageUri
import com.appcore.core.ui.utils.document_picker.rememberCameraLauncher
import com.appcore.core.ui.utils.document_picker.rememberDocumentPickerLauncher
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.screens.planvisit.utils.toImageBitmap
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Step 3 (Figma 33:6234): visit summary, voice note, photo attachment, "clinic open" confirmation. */
@Composable
fun ResultStepContent(
    state: PlanVisitState,
    onEvent: (PlanVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val cameraLauncher = rememberCameraLauncher(
        imageUri =  currentPhotoUri ?: Uri.EMPTY,
        onPicked = { document ->
            onEvent(PlanVisitEvents.AttachImageResult(document))
        }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        SummaryCard(
            state = state,
            onViewLocation = { onEvent(PlanVisitEvents.ViewLocationClick) },
        )
        VoiceNoteCard(
            isRecording = state.isRecordingVoice,
            hasRecording = state.visit.voiceNote != null,
            onRecordingStarted = { onEvent(PlanVisitEvents.VoiceNoteRecordingStarted) },
            onRecorded = { onEvent(PlanVisitEvents.VoiceNoteRecorded(it)) },
            onPermissionDenied = { onEvent(PlanVisitEvents.VoiceNotePermissionDenied) },
            onDelete = { onEvent(PlanVisitEvents.VoiceNoteDeleted) },
        )
        AttachPhotoCard(
            onAttach = {
                val newUri = createCameraImageUri(context)
                currentPhotoUri = newUri
                cameraLauncher.launch(newUri)
            },
            image = state.visit.imageNote?.toImageBitmap(context = context))
        ClinicOpenRow(
            checked = state.visit.noIssueFound,
            onCheckedChange = { onEvent(PlanVisitEvents.ToggleClinicOpen(it)) },
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun ResultStepContentPreview() {
    MedicalRepTheme {
        ResultStepContent(
            state = PlanVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ResultStepContentSingleEnglishPreview() {
    MedicalRepTheme {
        val state = PlanVisitFakes.state()
        ResultStepContent(
            state = state.copy(
                visit = state.visit.copy(isDoubleVisit = false),
            ),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
