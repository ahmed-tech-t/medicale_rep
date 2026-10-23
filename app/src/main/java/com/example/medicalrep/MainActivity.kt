package com.example.medicalrep

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.root.RootContent
import com.appcore.core.ui.utils.HandleUiEvents
import com.example.medicalrep.ui.navigation.AppNavHost
import com.example.medicalrep.ui.navigation.model.AuthGraph
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.root.AppRoot
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
                    fallback = rootViewModel.fallbackBootstrap(),
                    content = { bootstrap ->
                        val startDestination = when (bootstrap.authStatus) {
                            UserAuthStatus.Authenticated -> MainGraph.Root
                            UserAuthStatus.NotAuthenticated -> AuthGraph.Root
                        }
                        AppRoot(rootViewModel = rootViewModel, startDestination =  MainGraph.Root)
                    },
                )
            }
        }
    }
}

