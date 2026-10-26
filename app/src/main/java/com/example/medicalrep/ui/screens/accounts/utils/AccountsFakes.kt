package com.example.medicalrep.ui.screens.accounts.utils

import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.screens.accounts.main.AccountsState

/** Sample data shown until accounts are loaded from Room. */
object AccountsFakes {

    private fun account(
        id: String,
        name: String,
        type: AccountType,
        specialty: String,
        classCategory: String,
        address: String,
    ) = AccountUi(
        id = id,
        name = name,
        entityType = type.name,
        type = type,
        classCategory = classCategory,
        specialty = specialty,
        city = "Shebin El-Kom",
        address = address,
    )

    fun accounts(): List<AccountUi> = listOf(
        account("a1", "Dr. Mahmoud Abdelrahman Clinic", AccountType.Clinic, "Internal Medicine & GI", "A", "Doctors Tower, 3rd floor"),
        account("a2", "Dr. Hany El-Mesiry Clinic", AccountType.Clinic, "Internal Medicine", "B", "El-Bahr St."),
        account("a3", "Dr. Sara Fathy Clinic", AccountType.Clinic, "Pediatrics", "A", "Gamal Abdel Nasser St."),
        account("a4", "Dr. Karim Adel Clinic", AccountType.Clinic, "Orthopedics", "C", "El-Gesh St."),
        account("a5", "Shebin El-Kom University Hospital", AccountType.Hospital, "Cardiology", "A", "University Rd."),
        account("a6", "Delta Hospital", AccountType.Hospital, "General", "B", "El-Sadat St."),
        account("a7", "El-Shifa Pharmacy", AccountType.Pharmacy, "Pharmacy", "A", "Main Square"),
        account("a8", "Al-Nour Pharmacy", AccountType.Pharmacy, "Pharmacy", "B", "El-Gomhoreya St."),
        account("a9", "El-Amal Medical Center", AccountType.MedicalCenter, "Multi-specialty", "B", "El-Bahr St."),
        account("a10", "Nile Medical Center", AccountType.MedicalCenter, "Multi-specialty", "C", "Station St."),
    )

    fun state(): AccountsState = AccountsState(resultCount = accounts().size)
}
