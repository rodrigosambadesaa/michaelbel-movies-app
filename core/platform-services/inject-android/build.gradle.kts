plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.michaelbel.movies.platform.inject_android"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    flavorDimensions += "version"

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    productFlavors {
        create("gms") {
            dimension = "version"
            isDefault = true
        }
        create("hms") {
            dimension = "version"
        }
        create("foss") {
            dimension = "version"
        }
    }

}

val gmsImplementation by configurations
val hmsImplementation by configurations
val fossImplementation by configurations
dependencies {
    implementation(projects.core.platformServices.interactor)
    gmsImplementation(projects.core.platformServices.gms)
    hmsImplementation(projects.core.platformServices.hms)
    fossImplementation(projects.core.platformServices.foss)
}
