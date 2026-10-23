package com.appcore.core.data.di.annotation

import javax.inject.Qualifier

/** Bare OkHttp/Retrofit without auth interceptors, for the refresh-token call. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshClient

/** App-specific OkHttp interceptors added to the main client, e.g. `@Provides @IntoSet @AppInterceptor`. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppInterceptor
