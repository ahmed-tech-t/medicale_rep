package com.example.medicalrep.ui.components.widgets

import com.appcore.core.ui.theme.radii
import androidx.compose.material3.DividerDefaults

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.ConnectionStatusUiModel
import com.example.medicalrep.ui.models.StatusTone
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Success
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.OnSuccessContainer
import com.example.medicalrep.ui.ui.theme.Warning

@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: StatusTone = StatusTone.Neutral,
    showDot: Boolean = false,
    /** The Figma request pill: white with a grey border, only the dot carries the tone. */
    bordered: Boolean = false,
) {
    val (bg, fg) = when (tone) {
        StatusTone.Success -> OnSuccessContainer.copy(alpha = 0.15f) to OnSuccessContainer
        StatusTone.Warning -> Warning.copy(alpha = 0.18f) to Warning
        StatusTone.Danger -> Error.copy(alpha = 0.15f) to Error
        StatusTone.Neutral -> Slate100 to Slate500
    }
    val hasBorder = bordered || tone == StatusTone.Neutral
    Row(
        modifier = modifier
            .background(if (bordered) Slate50 else bg, RoundedCornerShape(radii.pill))
            .border(
                width = if (hasBorder) DividerDefaults.Thickness else 0.dp,
                color = if (hasBorder) Slate200 else Color.Transparent,
                shape = RoundedCornerShape(radii.pill),
            )
            .padding(horizontal = if (bordered) 12.dp else 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (bordered) 6.dp else 4.dp),
    ) {
        // Dot at start (right in RTL), then text
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(if (bordered) 7.dp else 6.dp)
                    .background(fg, CircleShape),
            )
        }
        NexoraText(
            text = text,
            style = if (bordered) MaterialTheme.typography.bodyMedium
            else MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                lineHeight = 13.5.sp,
            ),
            color = if (bordered) Ink else fg,
            textAlign = TextAlign.Start,
        )
    }
}

