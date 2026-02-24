plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "org.michaelbel.movies.notifications"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.interactor)
            implementation(projects.core.ui)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
