package com.example.medicalrep.ui.screens.sampleandgifts.main

sealed interface SampleAndGiftsEvents {
    data class SelectTab(val tab: SampleAndGiftsTab) : SampleAndGiftsEvents
}
