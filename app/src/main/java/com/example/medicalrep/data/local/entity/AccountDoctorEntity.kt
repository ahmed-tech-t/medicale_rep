package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** The many-to-many link: a doctor works at an account. Removing either side removes only the link. */
@Entity(
    tableName = "account_doctors",
    primaryKeys = ["accountId", "doctorId"],
    foreignKeys = [
        ForeignKey(AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DoctorEntity::class, parentColumns = ["id"], childColumns = ["doctorId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("doctorId")],
)
data class AccountDoctorEntity(
    val accountId: String,
    val doctorId: String,
)
