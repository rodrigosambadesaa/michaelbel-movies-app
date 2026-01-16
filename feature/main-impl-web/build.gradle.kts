@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    js { browser() }
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(projects.feature.feedWeb)
            api(libs.bundles.jetbrains.androidx.navigation3.common)
            api(libs.bundles.koin.common)
            implementation(libs.bundles.jetbrains.compose.material3.common)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
