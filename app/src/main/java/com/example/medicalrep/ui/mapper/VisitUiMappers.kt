package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi

/** The form fields of a visit being filled in, ready to be stored as a draft. */
fun VisitUi.toDomain() = Visit(
    id = id.ifBlank { null },
    city = city,
    region = region,
    address = address,
    account = account,
    placeName = placeName,
    specialty = specialty,
    shift = shift,
    type = type,
    visitMode = if (isDoubleVisit) VisitMode.Double else VisitMode.Single,
    supervisorName = supervisorName,
    classCategory = classCategory,
    gifts = gifts.map { it.toDomain() },
    products = products.map { it.toDomain() },
    voiceNote = voiceNote,
    imageNote = imageNote?.toString(),
    noIssueFound = noIssueFound,
    visitLatitude = visitLatitude,
    visitLongitude = visitLongitude,
    accountLatitude = accountLatitude,
    accountLongitude = accountLongitude,
    travelSeconds = travelSeconds,
)

private fun ProductUi.toDomain() = Product(
    id = id,
    name = name,
    quantity = quantity,
    kind = kind,
    description = description,
    remaining = remaining,
    price = price,
    discountPercent = discountPercent,
)
