package com.example.medicalrep.ui.components.widgets.filter

import androidx.annotation.StringRes
import com.example.medicalrep.R

/** Which upload state of a visit to list. */
sealed interface SyncFilter {
    @get:StringRes
    val valueRes: Int

    data object All : SyncFilter {
        override val valueRes = R.string.filter_all
    }

    data object Synced : SyncFilter {
        override val valueRes = R.string.filter_synced_done
    }

    data object Waiting : SyncFilter {
        override val valueRes = R.string.filter_sync_waiting
    }

    data object Failed : SyncFilter {
        override val valueRes = R.string.filter_sync_failed
    }

    companion object {
        val entries: List<SyncFilter> = listOf(All, Synced, Waiting, Failed)
    }
}