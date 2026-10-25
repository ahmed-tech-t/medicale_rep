plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.appcore.core"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // android.util.Log is a stub in JVM unit tests.
        unitTests.isReturnDefaultValues = true
    }
    buildTypes {
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            // :core has no benchmark build type; consume its release variant.
            matchingFallbacks += listOf("release")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        // Kotlin 2.4 default module names contain ':' which breaks KSP/Hilt code generation.
        moduleName.set("${rootProject.name}_core")
    }
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.metadata)
    }
}

dependencies {
    api(libs.androidx.datastore)
    api(libs.kotlinx.serialization.json)
    api(libs.okhttp)
    api(libs.retrofit)
    api(libs.retrofit.kotlinx.serialization)
    api(libs.okhttp.logging)
    api(libs.androidx.window.core)
    implementation(libs.androidx.activity.compose)
    debugApi(libs.chucker)
    releaseApi(libs.chucker.no.op)
    "benchmarkApi"(libs.chucker.no.op)

    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.viewmodel.ktx)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.ui.tooling.preview)
    debugApi(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
