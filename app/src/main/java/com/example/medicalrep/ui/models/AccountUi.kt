package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.SyncStatus

@Immutable
data class AccountUi(
    val id: String = "",
    val name: String = "",

    val accountType: BasicModelUi? = null,
    /** The kind of account read from [accountType]; null when it is not one of the known kinds. */
    val type: AccountType? = null,
    val classCategory:  BasicModelUi? = null,
    val specialty: BasicModelUi? = null,
    val city: BasicModelUi? = null,
    val district: BasicModelUi? = null,
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val supervisorId: String? = null,

    val isSynced: Boolean = false,
)
