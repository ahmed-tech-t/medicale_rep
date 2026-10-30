package com.example.medicalrep.ui.components.widgets.visit

import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.theme.radii
import androidx.compose.material3.DividerDefaults

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.models.StatusTone
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate300
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.Success
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.OnSuccessContainer
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate600

/** Shared surface chrome for visit cards (shadow + border + fill). */
@Composable
internal fun Modifier.visitCardChrome(
    elevation: Dp,
    borderAlpha: Float = 1f,
    shadowAlpha: Float = 0.03f,
): Modifier {
    val shape = RoundedCornerShape(radii.extraLarge)
    return this
        .fillMaxWidth()
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = shadowAlpha),
            spotColor = Color.Black.copy(alpha = shadowAlpha),
        )
        .border(DividerDefaults.Thickness, Slate200.copy(alpha = borderAlpha), shape)
        .background(Slate50, shape)
}

@Composable
internal fun Modifier.visitCardSurface(
    elevation: Dp,
    contentPadding: Dp,
    borderAlpha: Float = 1f,
    shadowAlpha: Float = 0.03f,
): Modifier = visitCardChrome(elevation, borderAlpha, shadowAlpha).padding(contentPadding)

@Composable
internal fun VisitStatusDot(tone: StatusTone, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(spacing.spaceSmall)
            .background(visitStatusDotColor(tone), CircleShape),
    )
}

@Composable
internal fun VisitDurationChip(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Slate100, RoundedCornerShape(radii.extraLarge))
            .padding(horizontal = 10.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        NexoraIcon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = stringResource(R.string.cd_clock),
            modifier = Modifier.size(10.dp),
            tint = Slate600,
        )
        NexoraText(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
            color = Ink,
        )
    }
}

/** Bordered class chip (Planning header). */
@Composable
internal fun VisitClassChip(classCategory: String, modifier: Modifier = Modifier) {
    NexoraText(
        text = stringResource(R.string.class_chip_format, classCategory),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
        ),
        color = Info,
        textAlign = TextAlign.Start,
        modifier = modifier
            .border(DividerDefaults.Thickness, Slate300, RoundedCornerShape(radii.extraSmall))
            .background(BrandTint, RoundedCornerShape(radii.extraSmall))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

/** Soft tag chip (Unplanned account/specialty). */
@Composable
internal fun VisitTagChip(text: String, modifier: Modifier = Modifier) {
    NexoraText(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp,
            lineHeight = 13.5.sp,
        ),
        overflow = TextOverflow.Ellipsis,
        color = Slate500,
        modifier = modifier

            .background(Slate100, RoundedCornerShape(radii.extraSmall))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

/** Specialty + class line under Planned title. */
@Composable
internal fun VisitSpecialtyClassLine(
    specialty: String,
    classCategory: String,
    modifier: Modifier = Modifier,
) {
    if (specialty.isBlank() && classCategory.isBlank()) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (specialty.isNotBlank()) {
            NexoraText(
                text = if (classCategory.isNotBlank()) "$specialty • " else specialty,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    lineHeight = 16.5.sp,
                ),
                color = Slate400,
            )
        }
        if (classCategory.isNotBlank()) {
            NexoraText(
                text = stringResource(R.string.class_only_format, classCategory),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    lineHeight = 16.5.sp,
                ),
                color = Slate600,
            )
        }
    }
}

internal fun visitStatusDotColor(tone: StatusTone): Color = when (tone) {
    StatusTone.Success -> OnSuccessContainer
    StatusTone.Warning -> OnWarningContainer
    StatusTone.Danger -> Color(0xFFDC2626)
    StatusTone.Neutral -> Slate400
}

/** Pill chip for visit facts (double visit, date/time, item counts). */
@Composable
internal fun VisitInfoChip(text: String, modifier: Modifier = Modifier) {
    NexoraChip(
        text = text,
        container = Slate100,
        content = Slate600,
        shape = RoundedCornerShape(radii.pill),
        modifier = modifier,
    )
}
