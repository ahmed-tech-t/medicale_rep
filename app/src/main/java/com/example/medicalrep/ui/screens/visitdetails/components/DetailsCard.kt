package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.DetailsBorder
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** White bordered card shared by the three sections of the details screen. */
@Composable
fun DetailsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = DetailsBorder,
        contentPadding = PaddingValues(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall + spacing.spaceExtraExtraSmall), content = content)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun DetailsCardPreview() {
    MedicalRepTheme { DetailsCard { NexoraText("محتوى") } }
}
