package com.example.medicalrep.ui.preview

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

/** A list wrapped the way a screen receives its paged items, for previews and UI tests. */
@Composable
fun <T : Any> pagingItemsOf(items: List<T>): LazyPagingItems<T> = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
