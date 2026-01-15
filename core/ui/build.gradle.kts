plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget() // fixme класс R
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.core.persistence)
            api(libs.bundles.coil.common)
            api(libs.bundles.jetbrains.androidx.lifecycle.common)
            api(libs.bundles.jetbrains.androidx.navigation3.common)
            api(libs.bundles.jetbrains.compose.common)
            api(libs.bundles.jetbrains.androidx.core.common)
            api(libs.jetbrains.compose.animation)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.runtime.saveable)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.material)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.compose.material.icons.extended)
        }
        androidMain.dependencies {
            api(libs.bundles.core.splashscreen.android)
            api(libs.bundles.palette.android)
            api(libs.bundles.coil.android)
            api(libs.bundles.compose.android)
            api(libs.bundles.google.material.android)
            implementation(libs.bundles.paging.android)
        }
        jvmMain.dependencies {
            api(compose.desktop.common)
            api(compose.desktop.currentOs)
            api(libs.bundles.compose.desktop)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}

android {
    namespace = "org.michaelbel.movies.ui"

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }
}
