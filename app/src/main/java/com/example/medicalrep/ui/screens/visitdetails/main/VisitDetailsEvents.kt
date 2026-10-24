package com.example.medicalrep.ui.screens.visitdetails.main

sealed interface VisitDetailsEvents {
    data object Back : VisitDetailsEvents
    data object ViewOnMap : VisitDetailsEvents
}
