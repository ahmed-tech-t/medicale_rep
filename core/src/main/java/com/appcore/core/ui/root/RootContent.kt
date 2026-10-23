package com.appcore.core.ui.root

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.domain.models.AppBootstrap
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.utils.Resource
import com.appcore.core.ui.components.LanguageWrapper

/**
 * Shows [splash] while bootstrapping, then [content] with the result
 * (or [fallback] when bootstrap failed). The app's content maps it to its NavHost start route.
 */
@Composable
fun RootContent(
    state: RootState,
    languageCode: String,
    fallback: AppBootstrap,
    content: @Composable (AppBootstrap) -> Unit,
) {
    when (val resource = state.bootstrap) {
        is Resource.Loading -> Unit
        else -> LanguageWrapper(languageCode = languageCode) {
            content(resource.data ?: fallback)
        }
    }
}

private val previewBootstrap = AppBootstrap(
    authStatus = UserAuthStatus.Authenticated,
    showOnBoarding = false,
    language = "en",
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun RootContentLoadingPreview() {
    MaterialTheme {
        RootContent(
            state = RootState(),
            languageCode = "en",
            fallback = previewBootstrap,
            content = { Text(it.authStatus.toString()) },
        )
    }
}

@Preview(showBackground = true, name = "Bootstrapped")
@Composable
private fun RootContentSuccessPreview() {
    MaterialTheme {
        RootContent(
            state = RootState(bootstrap = Resource.Success(previewBootstrap)),
            languageCode = "ar",
            fallback = previewBootstrap,
            content = { Text("Signed in: ${it.authStatus is UserAuthStatus.Authenticated}") },
        )
    }
}
