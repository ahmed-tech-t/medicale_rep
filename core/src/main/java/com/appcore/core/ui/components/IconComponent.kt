package com.appcore.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconComponent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 24.dp,
    @DrawableRes iconRes: Int
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = "",
        tint = color,
        modifier = modifier.size(size)
    )
}

@Composable
fun IconComponent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 24.dp,
    imageVector: ImageVector
) {
    Icon(
        imageVector = imageVector,
        contentDescription = "",
        tint = color,
        modifier = modifier.size(size)
    )
}

@Composable
fun SmallIconComponent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 16.dp,
    imageVector: ImageVector
) {
    Icon(
        imageVector = imageVector,
        contentDescription = "",
        tint = color,
        modifier = modifier.size(size)
    )
}

@Composable
fun SmallIconComponent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 16.dp,
    @DrawableRes iconRes: Int
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = "",
        tint = color,
        modifier = modifier.size(size)
    )
}

@Preview(showBackground = true)
@Composable
private fun IconComponentPreview() {
    MaterialTheme {
        IconComponent(imageVector = Icons.Default.Lock, color = MaterialTheme.colorScheme.primary)
    }
}
