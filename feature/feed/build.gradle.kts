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
        namespace = "org.michaelbel.movies.feed"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.ui)
            api(projects.core.interactor)
            api(projects.core.platformServices.interactor)
        }
        androidMain.dependencies {
            implementation(libs.bundles.paging.android)
        }
        jvmMain.dependencies {
            implementation(libs.bundles.paging.desktop)
            implementation(libs.androidx.paging.compose)
        }
        iosMain.dependencies {
            implementation(libs.androidx.paging.compose)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
