plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "org.michaelbel.movies.work"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.interactor)
        }
        androidMain.dependencies {
            implementation(libs.bundles.work.android)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
