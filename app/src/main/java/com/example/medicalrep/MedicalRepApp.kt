package com.example.medicalrep

import android.app.Application
import com.appcore.core.data.local.settings.LocaleHelper
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.seed.DebugSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class MedicalRepApp : Application() {
    @Inject
    lateinit var localeHelper: LocaleHelper

    @Inject
    lateinit var debugSeeder: DebugSeeder

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        localeHelper.syncSavedLocale(this)
        // Debug: every launch starts from fresh sample data (the wipe also clears the seed marker, so the seed runs again).
        if (BuildConfig.DEBUG) CoroutineScope(Dispatchers.IO).launch {
            database.clearAllTables()
            debugSeeder.seedIfEmpty()
        }
        MapsInitializer.initialize(
            applicationContext,
            MapsInitializer.Renderer.LATEST,
        ) { }
    }
}