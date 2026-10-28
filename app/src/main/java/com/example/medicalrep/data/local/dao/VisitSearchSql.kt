package com.example.medicalrep.data.local.dao

/**
 * A visit is searched by its account's text, its supervisor's text and its date (yyyy-MM-dd), all taken from
 * the other tables, so it is never stale. Use it as `UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE ...`.
 */
const val VISIT_SEARCH_TEXT_SQL =
    "TRIM(COALESCE((SELECT a.searchText FROM accounts a WHERE a.id = visits.accountId), '')" +
        " || ' ' || COALESCE((SELECT u.searchText FROM reps u WHERE u.id = visits.supervisorId), '')" +
        " || ' ' || CASE WHEN visits.dateTimeMillis > 0" +
        " THEN strftime('%Y-%m-%d', visits.dateTimeMillis / 1000, 'unixepoch', 'localtime') ELSE '' END)"
