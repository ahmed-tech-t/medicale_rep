package com.example.medicalrep.ui.models

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

/**
 * Custody item — only quantities from backend; labels via format string resources.
 */
@Immutable
data class StaticsGiftsItemUi(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val receivedFormatRes: Int,
    val receivedQuantity: Int,
    val skuCount: Int,
    val remaining: Int,
    val dispatched: Int,
)
