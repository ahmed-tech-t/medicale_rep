package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500

@Composable
fun LabelWrapperComponent(
    modifier: Modifier = Modifier,
    title: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        color = Slate500,
        fontWeight = FontWeight.Normal
    ),
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = spacing.spaceSmall)
    ) {
        NexoraText(title, style = textStyle)
        content()
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun LabelWrapperComponentPreview() {
    MedicalRepTheme {
        LabelWrapperComponent(
            title = "عنوان الحقل",
            textStyle = MaterialTheme.typography.titleSmall
        ) {
            NexoraTextField(rememberTextFieldState())
        }
    }
}