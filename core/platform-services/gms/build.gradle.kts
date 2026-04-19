plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "org.michaelbel.movies.platform.gms"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
        androidResources.enable = true

        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.platformServices.interactor)
            implementation(projects.core.interactor)
        }
        androidMain.dependencies {
            api(libs.bundles.google.firebase.android)
            api(libs.bundles.google.services.android)
            api(libs.bundles.google.play.android)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
