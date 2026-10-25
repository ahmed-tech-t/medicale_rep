package com.example.medicalrep.ui.screens.addaccount.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.utils.document_picker.PickedDocument
import com.appcore.core.ui.utils.document_picker.createCameraImageUri
import com.appcore.core.ui.utils.document_picker.rememberCameraLauncher
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraImage
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle
import com.example.medicalrep.ui.screens.planvisit.utils.toImageBitmap
import com.example.medicalrep.ui.ui.theme.OnSuccessContainer
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate700
import com.example.medicalrep.ui.ui.theme.SuccessContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Camera-only photo of the clinic sign: capture, watermark caption, preview, retake. */
@Composable
internal fun SignPhotoCard(
    state: AddAccountState,
    onPhotoCaptured: (PickedDocument) -> Unit,
) {
    val context = LocalContext.current
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberCameraLauncher(
        imageUri = pendingUri ?: Uri.EMPTY,
        onPicked = onPhotoCaptured,
    )
    val image = remember(state.signPhoto) { state.signPhoto?.toImageBitmap(context) }
    val capturedAt = remember(state.capturedAtMillis) {
        SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(Date(state.capturedAtMillis))
    }
    val borderColor = Slate200
    val dashRadius = radii.large

    StepCard {
        StepCardTitle(stringResource(R.string.add_account_sign_title))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(dashRadius.toPx()),
                        style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))),
                    )
                }
                .padding(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            if (image == null) {
                NexoraText(
                    text = stringResource(R.string.add_account_sign_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate700,
                    modifier = Modifier.padding(vertical = spacing.spaceMedium),
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NexoraText(
                        text = stringResource(R.string.add_account_gps_watermark),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Slate700,
                    )
                    NexoraText(
                        text = stringResource(R.string.add_account_captured_format, capturedAt),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Slate700,
                    )
                }
                NexoraImage(
                    bitmap = image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                        .clip(RoundedCornerShape(radii.large)),
                )
                NexoraChip(
                    text = stringResource(R.string.add_account_photo_linked),
                    container = SuccessContainer,
                    content = OnSuccessContainer,
                )
            }
        }
        NexoraButton(
            text = stringResource(
                if (image == null) R.string.add_account_capture_photo else R.string.add_account_retake_photo
            ),
            variant = NexoraButtonVariant.Muted,
            onClick = {
                val newUri = createCameraImageUri(context)
                pendingUri = newUri
                cameraLauncher.launch(newUri)
            },
        )
    }
}
