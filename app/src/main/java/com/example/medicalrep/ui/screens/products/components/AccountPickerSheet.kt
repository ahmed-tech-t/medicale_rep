package com.example.medicalrep.ui.screens.products.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.SearchBarComponent
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.accounts.components.AccountCard
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Bottom sheet that asks which account an order is for: a searchable list, one tap chooses. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    accounts: LazyPagingItems<AccountUi>,
    query: String,
    onQueryChange: (String) -> Unit,
    onSelect: (AccountUi) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Slate50,
    ) {
        AccountPickerContent(accounts, query, onQueryChange, onSelect, Modifier.navigationBarsPadding())
    }
}

@Composable
private fun AccountPickerContent(
    accounts: LazyPagingItems<AccountUi>,
    query: String,
    onQueryChange: (String) -> Unit,
    onSelect: (AccountUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
        NexoraText(
            text = stringResource(R.string.products_pick_account_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Ink,
            modifier = Modifier.padding(horizontal = spacing.spaceSmallPlus),
        )
        SearchBarComponent(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = stringResource(R.string.products_search_hint),
            modifier = Modifier.padding(horizontal = spacing.spaceSmallPlus),
        )
        LazyColumn(
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = accounts.itemCount, key = accounts.itemKey { it.id }) { index ->
                accounts[index]?.let { account ->
                    AccountCard(account = account, modifier = Modifier.clickable { onSelect(account) })
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 700)
@Composable
private fun AccountPickerContentPreview() {
    MedicalRepTheme {
        AccountPickerContent(
            accounts = pagingItemsOf(AccountsFakes.accounts()),
            query = "",
            onQueryChange = {},
            onSelect = {},
        )
    }
}
