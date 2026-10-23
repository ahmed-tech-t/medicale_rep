package com.example.medicalrep

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.utils.Resource
import com.appcore.core.ui.root.RootContent
import com.example.medicalrep.ui.screens.root.RootViewModel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { rootViewModel.state.value.bootstrap is Resource.Loading }
        setContent {
            MedicalRepTheme {
                val state by rootViewModel.state.collectAsStateWithLifecycle()
                RootContent(
                    state = state,
                    languageCode = rootViewModel.languageState,
                    fallback = rootViewModel.fallbackBootstrap(),
                    content = { bootstrap ->
                        // Choose the NavHost start route
                        when (bootstrap.authStatus) {
                            UserAuthStatus.Authenticated -> { /* home */ }
                            UserAuthStatus.NotAuthenticated -> { /* login */ }
                        }
                    },
                )
            }
        }
    }
}
