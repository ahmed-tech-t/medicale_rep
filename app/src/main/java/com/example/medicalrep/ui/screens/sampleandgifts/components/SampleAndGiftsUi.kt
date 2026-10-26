package com.example.medicalrep.ui.screens.sampleandgifts.components

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
import com.example.medicalrep.ui.models.CustodyUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.chrome.NexoraTabs
import com.example.medicalrep.ui.screens.sampleandgifts.main.SampleAndGiftsEvents
import com.example.medicalrep.ui.screens.sampleandgifts.main.SampleAndGiftsState
import com.example.medicalrep.ui.screens.sampleandgifts.main.SampleAndGiftsTab
import com.example.medicalrep.ui.screens.sampleandgifts.utils.SampleAndGiftsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 57:12182: rep custody with Samples / Gifts tabs. */
@Composable
fun SampleAndGiftsUi(
    state: SampleAndGiftsState,
    items: LazyPagingItems<CustodyUi>,
    onEvent: (SampleAndGiftsEvents) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(title = stringResource(R.string.sample_and_gifts_title), onBack = onBack)
        NexoraTabs(
            labels = SampleAndGiftsTab.entries.map { tab ->
                stringResource(
                    when (tab) {
                        SampleAndGiftsTab.Samples -> R.string.sample_and_gifts_tab_samples_format
                        SampleAndGiftsTab.Gifts -> R.string.sample_and_gifts_tab_gifts_format
                    },
                )
            },
            selectedIndex = state.selectedTab.ordinal,
            onSelect = { onEvent(SampleAndGiftsEvents.SelectTab(SampleAndGiftsTab.entries[it])) },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = items.itemCount, key = items.itemKey { it.id }) { index ->
                items[index]?.let { SampleAndGiftsItemCard(item = it) }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun SampleAndGiftsUiSamplesPreview() {
    MedicalRepTheme {
        SampleAndGiftsUi(
            state = SampleAndGiftsState(),
            items = pagingItemsOf(SampleAndGiftsFakes.samples),
            onEvent = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun SampleAndGiftsUiGiftsPreview() {
    MedicalRepTheme {
        SampleAndGiftsUi(
            state = SampleAndGiftsState(selectedTab = SampleAndGiftsTab.Gifts),
            items = pagingItemsOf(SampleAndGiftsFakes.gifts),
            onEvent = {},
            onBack = {},
        )
    }
}
