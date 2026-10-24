package com.example.medicalrep.ui.screens.planvisit.components.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraImage
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

@Composable
internal fun AttachPhotoCard(onAttach: () -> Unit, image: ImageBitmap?) {
    StepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(dim.thumbnailSize)
                    .clip(RoundedCornerShape(radii.medium))
                    .background(Slate25)
                    .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.medium)),
                contentAlignment = Alignment.Center,
            ) {
                image?.let {
                    NexoraImage(
                        bitmap = image,
                        contentDescription = null,
                        modifier = Modifier.size(dim.thumbnailSize),
                    )
                } ?: NexoraIcon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = Ink,
                    modifier = Modifier.size(dim.smallIconSize),
                )


            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraExtraSmall),
            ) {
                NexoraText(
                    text = stringResource(R.string.unplanned_attach_title),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                )
                NexoraText(
                    text = stringResource(R.string.unplanned_attach_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                )
            }
            Box(
                modifier = Modifier
                    .size(width = dim.compactActionWidth, height = dim.compactActionHeight)
                    .clip(RoundedCornerShape(radii.small))
                    .background(Brand)
                    .clickable(role = Role.Button, onClick = onAttach),
                contentAlignment = Alignment.Center,
            ) {
                NexoraIcon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = stringResource(R.string.unplanned_attach_title),
                    tint = Slate50,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AttachPhotoCardArabicPreview() {
    MedicalRepTheme { AttachPhotoCard(onAttach = {}, image = null) }
}

@Preview(showBackground = true)
@Composable
private fun AttachPhotoCardEnglishPreview() {
    MedicalRepTheme { AttachPhotoCard(onAttach = {}, image = null) }
}


