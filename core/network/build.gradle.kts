import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.buildkonfig)
}

private val tmdbApiKey: String by lazy {
    gradleLocalProperties(rootDir, providers).getProperty("TMDB_API_KEY").orEmpty().ifEmpty {
        System.getenv("TMDB_API_KEY").orEmpty()
    }
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "org.michaelbel.movies.network"
        minSdk = libs.versions.min.sdk.get().toInt()
        compileSdk = libs.versions.compile.sdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.bundles.kotlinx.serialization.common)
            implementation(libs.bundles.ktor.common)
        }
        androidMain.dependencies {
            implementation(libs.bundles.ktor.android)
            implementation(libs.bundles.startup.android)
            implementation(libs.bundles.okhttp.logging.interceptor.android)
            implementation(libs.bundles.chucker.library.no.op.android)
            implementation(libs.bundles.flaker.noop.android)
        }
        iosMain.dependencies {
            implementation(libs.bundles.ktor.ios)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}

buildkonfig {
    packageName = "org.michaelbel.movies.network"

    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", tmdbApiKey)
    }
}
