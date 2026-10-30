package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicalrep.ui.ui.theme.Cairo
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/**
 * Single controlled text atom. Always uses Cairo. No internal state.
 */
@Composable
fun NexoraText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(fontFamily = Cairo),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraTextPreview() {
    MedicalRepTheme {
        NexoraText(
            text = "نص مشترك",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
