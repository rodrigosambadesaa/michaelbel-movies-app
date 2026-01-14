import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.platformServices.interactor)
        }
    }

    androidLibrary {
        namespace = "org.michaelbel.movies.platform.hms"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
