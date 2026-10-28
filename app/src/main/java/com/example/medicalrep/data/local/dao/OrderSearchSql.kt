package com.example.medicalrep.data.local.dao

/**
 * An order is searched by its account's text, its rep's text and its date (yyyy-MM-dd), all taken from the
 * other tables, so it is never stale. Use it as `UPDATE orders SET searchText = $ORDER_SEARCH_TEXT_SQL WHERE ...`.
 */
const val ORDER_SEARCH_TEXT_SQL =
    "TRIM(COALESCE((SELECT a.searchText FROM accounts a WHERE a.id = orders.accountId), '')" +
        " || ' ' || COALESCE((SELECT u.searchText FROM reps u WHERE u.id = orders.repId), '')" +
        " || ' ' || CASE WHEN orders.createdAtMillis > 0" +
        " THEN strftime('%Y-%m-%d', orders.createdAtMillis / 1000, 'unixepoch', 'localtime') ELSE '' END)"
