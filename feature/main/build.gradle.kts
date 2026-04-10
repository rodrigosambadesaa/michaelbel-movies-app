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

    android {
        namespace = "org.michaelbel.movies.main"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.feature.account)
            api(projects.feature.auth)
            api(projects.feature.details)
            api(projects.feature.fave)
            api(projects.feature.feed)
            api(projects.feature.gallery)
            api(projects.feature.notify)
            api(projects.feature.settings)
            api(projects.feature.debug)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
