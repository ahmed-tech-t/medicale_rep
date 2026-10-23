package com.appcore.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appcore.core.R

@Composable
fun SnackBarComponent(
    modifier: Modifier = Modifier,
    data: SnackbarData,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    outerPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    dismissEndPadding: Dp = 16.dp,
    dismissButtonSize: Dp = 20.dp,
    dismissIconSize: Dp = 16.dp,
) {
    Box(Modifier.padding(outerPadding)) {
        Snackbar(
            shape = RoundedCornerShape(10.dp),
            containerColor = containerColor,
            dismissAction = {
                IconButton(
                    onClick = data::dismiss,
                    modifier = Modifier
                        .padding(end = dismissEndPadding)
                        .size(dismissButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = contentColor,
                        modifier = Modifier.size(dismissIconSize)
                    )
                }
            }
        ) {
            CText(
                modifier = modifier,
                text = data.visuals.message,
                textColor = contentColor,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = contentColor
                ),
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SnackBarComponentPreview() {
    val sampleData = object : SnackbarData {
        override val visuals: SnackbarVisuals = object : SnackbarVisuals {
            override val message: String = "Property added successfully!"
            override val actionLabel: String? = null
            override val withDismissAction: Boolean = false
            override val duration: SnackbarDuration = SnackbarDuration.Short
        }

        override fun dismiss() {}
        override fun performAction() {}
    }
    MaterialTheme {
        SnackBarComponent(data = sampleData)
    }
}
