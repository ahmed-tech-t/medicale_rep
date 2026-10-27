package com.example.medicalrep.ui.mapper

import com.appcore.core.data.local.datastore.model.UserDS
import com.example.medicalrep.ui.models.UserUi

/** The signed-in user as Home and the drawer show him: full name, role, and the first letters as initials. */
fun UserDS.toUi() = UserUi(
    id = id?.toString(),
    displayName = "$firstName $lastName".trim(),
    roleTitle = roleName,
    initials = listOf(firstName, lastName).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString(""),
)
