package com.example.medicalrep.ui.screens.home.components.nav_drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

private val AvatarSize = 56.dp

/** Figma side drawer (node 33:7964): profile header, divided menu rows, language row. */
@Composable
fun NavDrawerSheet(
    user: UserUi,
    language: String,
    onItemClick: (NavDrawerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(.7f)
            .fillMaxHeight()
            .background(Brand)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(vertical = spacing.spaceMediumPlus)
            .testTag(NavDrawerTestTags.Sheet),
    ) {
        DrawerHeader(user = user)
        DrawerDivider()
        NavDrawerItem.menu.forEach { item ->
            DrawerRow(
                label = stringResource(item.labelRes),
                onClick = { onItemClick(item) },
            )
            DrawerDivider()
        }
        DrawerRow(
            label = stringResource(NavDrawerItem.Language.labelRes),
            onClick = { onItemClick(NavDrawerItem.Language) },
            trailing = { LanguageTag(language = language) },
        )
    }
}

@Composable
private fun DrawerHeader(user: UserUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(AvatarSize)
                .clip(CircleShape)
                .background(Slate50.copy(alpha = 0.08f))
                .border(DividerDefaults.Thickness, Slate50.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            NexoraIcon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Slate50,
                modifier = Modifier.size(dim.iconSize),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraExtraSmall),
        ) {
            NexoraText(
                text = user.displayName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            NexoraText(
                text = user.specialty,
                style = MaterialTheme.typography.labelMedium,
                color = BrandSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            NexoraText(
                text = stringResource(R.string.drawer_id_format, user.employeeCode),
                style = MaterialTheme.typography.labelSmall,
                color = BrandSoft,
            )
        }
    }
}

@Composable
private fun DrawerRow(
    label: String,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Slate50,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Composable
private fun LanguageTag(language: String) {
    val isArabic = language.toLowerCase(Locale.current) == "ar"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
    ) {
        NexoraIcon(
            painter = painterResource(if (isArabic) R.drawable.ic_us else R.drawable.ic_egypt),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(dim.smallIconSize),
        )
        NexoraText(
            text = if (isArabic) "(EN)" else "(AR)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate50,
        )
    }
}

@Composable
private fun DrawerDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = spacing.spaceMedium),
        color = Slate50.copy(alpha = 0.12f),
    )
}

object NavDrawerTestTags {
    const val Sheet = "nav_drawer_sheet"
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun NavDrawerSheetArabicPreview() {
    MedicalRepTheme {
        NavDrawerSheet(user = HomeSamples.user().toUi(), language = "ar", onItemClick = {})
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun NavDrawerSheetEnglishShortPreview() {
    MedicalRepTheme {
        NavDrawerSheet(user = HomeSamples.user().toUi(), language = "en", onItemClick = {})
    }
}
