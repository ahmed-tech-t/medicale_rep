package com.example.medicalrep.ui.screens.login.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600

/** Tappable pill that shows the language a tap switches to (e.g. "EN" while the app is Arabic). */
@Composable
fun LoginLanguagePill(
    code: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .clip(shape)
            .border(DividerDefaults.Thickness, MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        NexoraIcon(
            painter = painterResource(if (code.toLowerCase(Locale.current) == "ar") R.drawable.ic_egypt else R.drawable.ic_us),
            tint = Color.Unspecified,
            modifier = Modifier.size(dim.smallIconSize).clip(RoundedCornerShape(radii.small)),
        )
        NexoraText(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            color = Slate600,
        )
        NexoraIcon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(dim.smallIconSize),
            tint = Slate500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginLanguagePillPreview() {
    MedicalRepTheme {
        LoginLanguagePill(
            code = "EN",
            onClick = {},
            modifier = Modifier.padding(spacing.spaceMedium)
        )
    }
}
