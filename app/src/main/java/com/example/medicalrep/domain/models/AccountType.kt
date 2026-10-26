package com.example.medicalrep.domain.models

/** The kinds of account the accounts list can be filtered by. */
enum class AccountType {
    Clinic,
    Hospital,
    Pharmacy,
    MedicalCenter,
    ;

    companion object {
        /** Reads the stored free text (Arabic or English); null when it is blank or not one of the kinds. */
        fun fromEntityType(text: String): AccountType? {
            val value = text.lowercase()
            return when {
                "عياد" in value || "clinic" in value -> Clinic
                "مستشف" in value || "hospital" in value -> Hospital
                "صيدل" in value || "pharmac" in value -> Pharmacy
                "مركز" in value || "center" in value || "centre" in value -> MedicalCenter
                else -> null
            }
        }
    }
}
