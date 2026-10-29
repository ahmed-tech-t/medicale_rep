package com.example.medicalrep.ui.models

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType

@Immutable
data class VisitUi(
    val id: String = "",
    val backendId: String = "",
    /** The account the visit is for; empty until a place is chosen. */
    val accountId: String = "",

    val date: String = "",
    val time: String = "",
    val city: BasicModelUi? = null,
    val region: BasicModelUi? = null,
    val address: String = "",
    val account: BasicModelUi? = null,
    val placeName: String = "",
    val specialty: BasicModelUi? = null,
    val shift: Shift = Shift.Am,
    val visitLatitude: Double = 0.0,
    val visitLongitude: Double = 0.0,
    /** How long the rep took to reach the visit (from the GPS track); 0 when unknown. */
    val travelSeconds: Int = 0,
    val accountLatitude: Double = 0.0,
    val accountLongitude: Double = 0.0,
    val isDoubleVisit: Boolean = false,
    val type: VisitType = VisitType.Unplanned,
    val supervisor: BasicModelUi? = null,
    val classCategory: BasicModelUi? = null,
    val gifts: List<ProductUi> = emptyList(),
    val products: List<ProductUi> = emptyList(),
    val voiceNote: ByteArray? = null,
    val imageNote: Uri? = null,
    val noIssueFound: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
    val durationLabel: String = "",
)
