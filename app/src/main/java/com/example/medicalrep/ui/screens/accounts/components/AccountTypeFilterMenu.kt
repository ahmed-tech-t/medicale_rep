package com.example.medicalrep.ui.screens.accounts.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.accounts.utils.filterLabelRes
import com.example.medicalrep.ui.screens.accounts.utils.iconRes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Popup under the filter icon: All and every [AccountType], with a check on the selected one. */
@Composable
fun AccountTypeFilterMenu(
    expanded: Boolean,
    selected: AccountType?,
    onSelect: (AccountType?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = Color.White,
        shape = RoundedCornerShape(radii.large),
    ) {
        FilterOption(R.string.accounts_filter_all, R.drawable.ic_grid_all, selected == null) { onSelect(null) }
        AccountType.entries.forEach { type ->
            FilterOption(type.filterLabelRes, type.iconRes, selected == type) { onSelect(type) }
        }
    }
}

@Composable
private fun FilterOption(labelRes: Int, iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { NexoraText(text = stringResource(labelRes), color = Ink) },
        onClick = onClick,
        leadingIcon = {
            // Keeps the labels aligned: the check is invisible when the option is not selected.
            NexoraIcon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) Ink else Color.Transparent,
            )
        },
        trailingIcon = {
            NexoraIcon(painter = painterResource(iconRes), contentDescription = null, modifier = Modifier.size(16.dp), tint = Ink)
        },
    )
}

@Preview(showBackground = true, locale = "ar", heightDp = 320)
@Composable
private fun AccountTypeFilterMenuPreview() {
    MedicalRepTheme { AccountTypeFilterMenu(expanded = true, selected = null, onSelect = {}, onDismiss = {}) }
}

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun AccountTypeFilterMenuHospitalPreview() {
    MedicalRepTheme { AccountTypeFilterMenu(expanded = true, selected = AccountType.Hospital, onSelect = {}, onDismiss = {}) }
}
