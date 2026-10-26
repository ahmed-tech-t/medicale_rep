package com.example.medicalrep.ui.screens.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.screens.accounts.utils.iconRes
import com.example.medicalrep.ui.screens.accounts.utils.labelRes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** Shows an account: name and class, then specialty and location; the type chip sits at the opposite end. Not clickable. */
@Composable
fun AccountCard(
    account: AccountUi,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier, containerColor = Slate50, borderColor = Slate200) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                    NexoraText(
                        text = account.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Ink,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (account.classCategory.isNotBlank()) {
                        NexoraChip(
                            text = stringResource(R.string.unplanned_class_format, account.classCategory),
                            container = Slate100,
                            content = Slate600,
                        )
                    }
                }
                if (account.specialty.isNotBlank()) {
                    NexoraText(text = account.specialty, style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
                val location = listOf(account.city, account.address).filter { it.isNotBlank() }.joinToString(" · ")
                if (location.isNotEmpty()) {
                    NexoraText(text = location, style = MaterialTheme.typography.labelSmall, color = Slate400)
                }
            }
            account.type?.let { AccountTypeChip(it) }
        }
    }
}

@Composable
private fun AccountTypeChip(type: AccountType, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(1.dp, BrandBorder, RoundedCornerShape(radii.medium))
            .background(BrandTint, RoundedCornerShape(radii.medium))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NexoraIcon(painter = painterResource(type.iconRes), contentDescription = null, modifier = Modifier.size(14.dp), tint = Brand)
        NexoraText(text = stringResource(type.labelRes), style = MaterialTheme.typography.labelSmall, color = Brand)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AccountCardArabicPreview() {
    MedicalRepTheme { AccountCard(account = AccountsFakes.accounts().first(), modifier = Modifier.padding(spacing.spaceMedium)) }
}

@Preview(showBackground = true)
@Composable
private fun AccountCardHospitalPreview() {
    MedicalRepTheme { AccountCard(account = AccountsFakes.accounts()[4], modifier = Modifier.padding(spacing.spaceMedium)) }
}
