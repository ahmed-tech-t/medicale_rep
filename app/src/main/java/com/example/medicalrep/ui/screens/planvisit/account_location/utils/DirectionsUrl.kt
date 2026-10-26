package com.example.medicalrep.ui.screens.planvisit.account_location.utils

/** Google Maps link: opens the Maps app (or the browser) with driving directions from the user's location. */
fun directionsUrl(latitude: Double, longitude: Double): String =
    "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving"
