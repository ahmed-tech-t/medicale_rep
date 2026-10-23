package com.example.medicalrep

import android.app.Application
import com.appcore.core.data.local.settings.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class MedicalRepApp : Application() {
    @Inject
    lateinit var localeHelper: LocaleHelper

    override fun onCreate() {
        super.onCreate()
        localeHelper.applySavedLocale()
    }
}