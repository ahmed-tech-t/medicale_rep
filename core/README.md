# AppCore

Android library template (`:core`) for login sessions, networking, local storage, language settings, and common Compose UI.
Each app **copies** `core` into its own project. The copy belongs to that app: edits to it never change this repo or other apps.

- Package: `com.appcore.core`
- Stack: Kotlin 2.4, AGP 9.4, Compose, Hilt + KSP, Retrofit + OkHttp + kotlinx.serialization, DataStore

## 1. Copy core into an app

`core` only builds if the app's version catalog defines every alias that `core/build.gradle.kts` uses. Copying the folder alone fails with `Unresolved reference 'library'` / `'ksp'` / `'hilt'`. Do all four steps below.

### Step 1: Copy the folder

Copy `core/` into the app project, next to `app/`. Skip the `build/` folder:

```powershell
robocopy ..\andorid_core_app\core .\core /E /XD build
```

```
my_app/
├── app/
├── core/        <- the copy
├── gradle/libs.versions.toml
└── settings.gradle.kts
```

### Step 2: Merge the version catalog

Add these to `my_app/gradle/libs.versions.toml`. Skip any key the app already has, and keep the app's own version for it.

```toml
[versions]
agp = "9.4.1"
kotlin = "2.4.0"
ksp = "2.3.11"
hilt = "2.60.1"
kotlinMetadataJvm = "2.4.0"
appcompat = "1.7.1"
datastore = "1.2.1"
kotlinxSerializationJson = "1.11.0"
okhttp = "5.3.2"
retrofit = "2.11.0"
retrofitKotlinxSerialization = "1.0.0"
chucker = "4.2.0"
junit = "4.13.2"
coroutinesTest = "1.10.2"
lifecycle = "2.10.0"
composeBom = "2026.03.00"

[libraries]
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-datastore = { module = "androidx.datastore:datastore", version.ref = "datastore" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutinesTest" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
kotlin-metadata = { module = "org.jetbrains.kotlin:kotlin-metadata-jvm", version.ref = "kotlinMetadataJvm" }
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
okhttp-logging = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { module = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter", version.ref = "retrofitKotlinxSerialization" }
chucker = { module = "com.github.chuckerteam.chucker:library", version.ref = "chucker" }
chucker-no-op = { module = "com.github.chuckerteam.chucker:library-no-op", version.ref = "chucker" }
junit = { group = "junit", name = "junit", version.ref = "junit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

`kotlin` must be `2.4.0` (it must match `kotlinMetadataJvm`, which core forces).

### Step 3: Declare the plugins at the root

`my_app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

### Step 4: Include and depend on core

`my_app/settings.gradle.kts`:

```kotlin
include(":app")
include(":core")
```

`my_app/app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":core"))
}
```

Sync Gradle, then check with `gradlew :core:testDebugUnitTest :app:assembleDebug`.

### Optional: rename the package

The copy uses `com.appcore.core`. To use your own package, right-click `com.appcore.core` in Android Studio and choose **Refactor > Rename**, then update `namespace` in `core/build.gradle.kts`.

### Requirements for the app

- **Java/JVM target 21**, otherwise inline functions from core fail to compile:
  ```kotlin
  android {
      compileOptions {
          sourceCompatibility = JavaVersion.VERSION_21
          targetCompatibility = JavaVersion.VERSION_21
      }
  }
  ```
- **Hilt + KSP** in the app (core provides its classes through Hilt):
  ```kotlin
  plugins {
      alias(libs.plugins.ksp)
      alias(libs.plugins.hilt)
  }
  dependencies {
      implementation(libs.hilt.android)
      ksp(libs.hilt.compiler)
  }
  ```
- `INTERNET` permission in `AndroidManifest.xml`.

## 2. Provide what core needs

Each app must supply a `CoreConfig` and a `TokenRefresher`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppCoreModule {

    @Provides
    @Singleton
    fun provideCoreConfig() = CoreConfig(
        baseUrl = "https://your-api.com/api/",
        isDebug = BuildConfig.DEBUG,
        cryptoKeyAlias = "my_app_aes_key", // Keystore alias that encrypts the saved session
        defaultLanguage = "ar",
    )

    // Call your refresh-token endpoint here, using the @RefreshClient Retrofit.
    @Provides
    fun provideTokenRefresher(): TokenRefresher = TokenRefresher { refreshToken ->
        RefreshResult.InvalidSession
    }
}
```

`RefreshResult` options:
- `Success(accessToken, refreshToken)`: new tokens are saved.
- `InvalidSession`: the user must sign in again.
- `TransientFailure`: a network error. The session is kept and only this request fails.

Optional `CoreConfig` fields: `staticHeaders`, plus `languageHeader`, `countryHeader`, and `deviceIdHeader` (set any of them to `null` to stop sending that header).

## 3. Application class

```kotlin
@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var localeHelper: LocaleHelper

    override fun onCreate() {
        super.onCreate()
        localeHelper.applySavedLocale()
    }
}
```

Register it with `android:name=".MyApp"` in the manifest.

## 4. Root ViewModel (splash and session)

Extend `BaseRootViewModel`. Startup (saved session, settings, language) runs automatically.

```kotlin
@HiltViewModel
class RootViewModel @Inject constructor(
    bootstrapUseCase: BootstrapUseCase,
    authSessionManager: AuthSessionManager,
    appSettingsManager: AppSettingsManager,
) : BaseRootViewModel(bootstrapUseCase, authSessionManager, appSettingsManager) {

    // Extra startup work while the splash is visible (keep it short).
    override suspend fun loadDuringSplash(data: AppBootstrap) {}

    // Refresh token rejected: navigate to the sign-in screen.
    override suspend fun onSessionExpired(message: String?) {
        sendEvent(UiEvent.ShowSnackBar(UiText.DynamicText(message ?: "Session expired")))
    }
}
```

## 5. Activity

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val rootViewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val state by rootViewModel.state.collectAsStateWithLifecycle()
                RootContent(
                    state = state,
                    languageCode = rootViewModel.languageState,
                    fallback = rootViewModel.fallbackBootstrap(),
                    splash = { /* your splash UI */ },
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
```

The activity must be an `AppCompatActivity` with an AppCompat theme (for example `Theme.AppCompat.DayNight.NoActionBar`), because language switching depends on it.

## What core provides

| Area | Main classes |
|---|---|
| Networking | `CoreNetworkModule` (Retrofit/OkHttp), `AuthInterceptor`, `TokenAuthenticator`, `ClientHeadersInterceptor`, Chucker in debug builds |
| API helpers | `HandelApiRequest`, `handleHttpException`, `BaseResponse`, `Resource` |
| Session | `AuthSessionManager`, `SessionClearListener`, encrypted DataStore (`CryptoManager`) |
| Settings / language | `AppSettingsManager`, `LocaleHelper`, `LanguageWrapper` |
| UI | `RootContent`, `ScaffoldComponent`, `CTextField`, `Text`, `IconComponent`, `SnackBarComponent`, modifier extensions (shimmer, shadow, borders...) |
| Events | `UiEvent`, `UiText` |

## Testing core

```
gradlew :core:testDebugUnitTest
```

## Notes

- Each copy is independent. A fix made in one app's copy does not reach this repo or other apps. To share a fix, apply it here too, and copy it into other apps by hand if they need it.
- Keep backend-specific values (base URL, headers, key alias) in `CoreConfig` instead of editing core, so copies stay easy to update.
