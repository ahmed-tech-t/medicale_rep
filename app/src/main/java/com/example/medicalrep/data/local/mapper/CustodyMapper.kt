package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.CustodyEntity
import com.example.medicalrep.data.local.relation.CustodyWithPending
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Custody

fun CustodyEntity.toDomain(pending: Int = 0) = Custody(
    id = id,
    name = name,
    kind = kind,
    description = description,
    received = received,
    dispatched = dispatched,
    pending = pending,
)

fun CustodyWithPending.toDomain() = custody.toDomain(pending)

fun Custody.toEntity() = CustodyEntity(
    id = id,
    name = name,
    kind = kind,
    description = description,
    received = received,
    dispatched = dispatched,
    searchText = searchTextOf(name, description),
)
