package com.example.medicalrep.ui.screens.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.widgets.FilterMenu
import com.example.medicalrep.ui.components.widgets.accountTypeFilterOptions
import com.example.medicalrep.ui.components.widgets.ResultsFilterRow
import com.example.medicalrep.ui.screens.accounts.main.AccountsEvents
import com.example.medicalrep.ui.screens.accounts.main.AccountsOverlay
import com.example.medicalrep.ui.screens.accounts.main.AccountsState
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Accounts list with a results row; its filter icon opens the account type popup. */
@Composable
fun AccountsUi(
    state: AccountsState,
    accounts: LazyPagingItems<AccountUi>,
    onEvent: (AccountsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(title = stringResource(R.string.accounts), onBack = { onEvent(AccountsEvents.Back) })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            item(key = "results") {
                ResultsFilterRow(
                    count = state.resultCount,
                    onFilterClick = { onEvent(AccountsEvents.OpenFilter) },
                    isFiltered = state.isFiltered,
                    filterMenu = {
                        FilterMenu(
                            expanded = state.overlay == AccountsOverlay.TypeFilter,
                            options = accountTypeFilterOptions,
                            selected = state.selectedType,
                            onSelect = { onEvent(AccountsEvents.SelectType(it)) },
                            onDismiss = { onEvent(AccountsEvents.CloseFilter) },
                        )
                    },
                )
            }
            items(count = accounts.itemCount, key = accounts.itemKey { it.id }) { index ->
                accounts[index]?.let { AccountCard(account = it) }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 760)
@Composable
private fun AccountsUiArabicPreview() {
    MedicalRepTheme {
        AccountsUi(state = AccountsFakes.state(), accounts = pagingItemsOf(AccountsFakes.accounts()), onEvent = {})
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 760)
@Composable
private fun AccountsUiFilterOpenPreview() {
    MedicalRepTheme {
        AccountsUi(
            state = AccountsFakes.state().copy(overlay = AccountsOverlay.TypeFilter),
            accounts = pagingItemsOf(AccountsFakes.accounts()),
            onEvent = {},
        )
    }
}
