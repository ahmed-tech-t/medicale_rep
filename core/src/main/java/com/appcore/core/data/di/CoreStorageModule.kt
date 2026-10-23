package com.appcore.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.crypto.CryptoManager
import com.appcore.core.data.local.datastore.jsonDataStore
import com.appcore.core.data.local.datastore.model.AppSettingsDS
import com.appcore.core.data.local.datastore.model.AuthSessionDS
import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.data.local.datastore.serializer.EncryptedJsonSerializer
import com.appcore.core.data.local.datastore.serializer.JsonSerializer
import com.appcore.core.data.local.session.SessionClearListener
import com.appcore.core.data.repo.AppSettingsRepoImpl
import com.appcore.core.data.repo.AuthSessionLocalRepoImpl
import com.appcore.core.data.repo.DataStoreUserLocalRepo
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import com.appcore.core.domain.repo.UserLocalRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val AUTH_SESSION_FILE = "auth_session.pb"
private const val APP_SETTINGS_FILE = "appSettings.pb"
private const val USER_INFO_FILE = "userInfo.pb"

// Kept out of the Hilt graph so it can't clash with the app's own Json bindings.
private val storageJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CoreStorageModule {

    @Binds
    abstract fun bindAuthSessionLocalRepo(impl: AuthSessionLocalRepoImpl): AuthSessionLocalRepo

    @Binds
    abstract fun bindAppSettingsRepo(impl: AppSettingsRepoImpl): AppSettingsRepo

    /** Lets apps register zero or more listeners with `@IntoSet`. */
    @Multibinds
    abstract fun sessionClearListeners(): Set<SessionClearListener>

    companion object {
        @Provides
        @Singleton
        fun provideCryptoManager(config: CoreConfig): CryptoManager =
            CryptoManager(config.cryptoKeyAlias)

        @Provides
        @Singleton
        fun provideAuthSessionDataStore(
            @ApplicationContext context: Context,
            cryptoManager: CryptoManager,
        ): DataStore<AuthSessionDS> = context.jsonDataStore(
            fileName = AUTH_SESSION_FILE,
            serializer = EncryptedJsonSerializer(
                crypto = cryptoManager,
                json = storageJson,
                serializer = AuthSessionDS.serializer(),
                defaultValue = AuthSessionDS(),
            ),
        )

        @Provides
        @Singleton
        fun provideAppSettingsDataStore(
            @ApplicationContext context: Context,
            config: CoreConfig,
        ): DataStore<AppSettingsDS> = context.jsonDataStore(
            fileName = APP_SETTINGS_FILE,
            serializer = JsonSerializer(
                json = storageJson,
                serializer = AppSettingsDS.serializer(),
                defaultValue = AppSettingsDS(
                    language = config.defaultLanguage,
                ),
            ),
        )

        @Provides
        @Singleton
        fun provideUserLocalRepo(@ApplicationContext context: Context): UserLocalRepo<UserDS> =
            DataStoreUserLocalRepo(
                dataStore = context.jsonDataStore(
                    fileName = USER_INFO_FILE,
                    serializer = JsonSerializer(
                        json = storageJson,
                        serializer = UserDS.serializer(),
                        defaultValue = UserDS(),
                    ),
                ),
                default = UserDS(),
            )

        /** The cached profile belongs to the signed-in user, so it goes with the session. */
        @Provides
        @IntoSet
        fun provideClearUserOnSessionCleared(userLocalRepo: UserLocalRepo<UserDS>): SessionClearListener =
            SessionClearListener { userLocalRepo.clear() }
    }
}
