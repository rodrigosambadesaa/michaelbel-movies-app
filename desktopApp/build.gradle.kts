import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.nio.charset.StandardCharsets

private val desktopVersionName = "3.0.0"
private val gitCommitsCount by lazy {
    try {
        val isWindows = System.getProperty("os.name").contains("Windows", ignoreCase = true)
        val processBuilder = when {
            isWindows -> ProcessBuilder("cmd", "/c", "git", "rev-list", "--count", "HEAD")
            else -> ProcessBuilder("git", "rev-list", "--count", "HEAD")
        }
        processBuilder.redirectErrorStream(true)
        processBuilder.start().inputStream.bufferedReader(StandardCharsets.UTF_8).readLine().trim().toInt()
    } catch (_: Exception) {
        1
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(projects.core.platformServices.injectJvm)
            implementation(projects.feature.about)
            implementation(projects.feature.main)
            implementation(libs.slf4j.simple)
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.michaelbel.movies.MainWindowKt"
        jvmArgs += listOf(
            "-Dapple.awt.application.name=Movies",
            "-Xdock:name=Movies",
            "-Dmovies.version=$desktopVersionName",
            "-Dmovies.build=$gitCommitsCount"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Movies"
            packageVersion = desktopVersionName

            val iconsRoot = project.file("desktop-icons")
            macOS {
                bundleID = "org.michaelbel.movies"
                dockName = "Movies"
                iconFile.set(project.file("desktop-icons").resolve("movies_macos.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("movies-windows.ico"))
                menuGroup = "Movies Menu"
                upgradeUuid = "3e111aef-dba0-434e-82ca-a89155e2d306"
            }
            linux {
                iconFile.set(iconsRoot.resolve("movies-linux.png"))
            }
        }
    }
}

tasks.register("printVersionName") { doLast { println(compose.desktop.application.nativeDistributions.packageVersion) } }
