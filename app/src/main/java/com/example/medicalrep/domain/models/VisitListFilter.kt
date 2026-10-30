package com.example.medicalrep.domain.models

/**
 * What the visit lists can be narrowed by; null (and false) mean "do not narrow".
 * The period is [fromMillis] included to [toMillis] not included.
 * [query] is the search text (clinic, supervisor, date, doctor name or specialty); blank means everything.
 */
data class VisitListFilter(
    val shift: Shift? = null,
    val classCategory: String? = null,
    val cityId: String? = null,
    val districtId: String? = null,
    val specialty: String? = null,
    val fromMillis: Long? = null,
    val toMillis: Long? = null,
    val syncStatus: SyncStatus? = null,
    val visitMode: VisitMode? = null,
    val query: String = "",
) {
    /** The search is not a filter: it has its own box on the screen. */
    val isActive: Boolean get() = copy(query = "") != VisitListFilter()
}
