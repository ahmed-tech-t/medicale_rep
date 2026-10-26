package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import com.example.medicalrep.data.local.entity.CustodyEntity

/** A custody row plus the quantity given in visits that are not uploaded yet (so the offline balance is right). */
data class CustodyWithPending(
    @Embedded val custody: CustodyEntity,
    val pending: Int,
)
