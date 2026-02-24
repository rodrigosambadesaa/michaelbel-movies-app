plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "org.michaelbel.movies.main"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.feature.account)
            api(projects.feature.auth)
            api(projects.feature.details)
            api(projects.feature.feed)
            api(projects.feature.gallery)
            api(projects.feature.settings)
            api(projects.feature.debug)
            implementation(libs.bundles.jetbrains.androidx.lifecycle.viewmodel.common)
        }
        jvmMain.dependencies {
            implementation(projects.core.platformServices.injectJvm)
        }
        iosMain.dependencies {
            implementation(projects.core.platformServices.injectIos)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
