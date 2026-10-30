package com.example.medicalrep.domain.models

/**
 * What the visit lists can be narrowed by; null (and false) mean "do not narrow".
 * The period is [fromMillis] included to [toMillis] not included.
 * [nearLatitude]/[nearLongitude] sort the list nearest to that point first; they do not narrow it.
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
    val nearLatitude: Double? = null,
    val nearLongitude: Double? = null,
) {
    /** The search and the sort point are not filters: the search has its own box, the sort point is the rep's place. */
    val isActive: Boolean get() = copy(query = "", nearLatitude = null, nearLongitude = null) != VisitListFilter()
}
