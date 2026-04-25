import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.appdistribution) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.palantir.git)
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.file(".github/detekt.yml"))
        buildUponDefaultConfig = true
        source.setFrom(
            project.files("src").asFileTree.matching {
                include("**/*.kt")
            }
        )
    }
    dependencies {
        val catalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        "detektPlugins"(catalog.findLibrary("detekt-rules").get())
    }
}
