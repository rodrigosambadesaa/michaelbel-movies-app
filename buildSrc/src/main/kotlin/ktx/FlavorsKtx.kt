package ktx

import org.gradle.api.Project

val Project.isGmsBuild: Boolean
    get() {
        return gradle.startParameter.taskNames.any { name ->
            name.contains("GmsDebug", ignoreCase = true) || name.contains("GmsRelease", ignoreCase = true) || name.contains("GmsBenchmark", ignoreCase = true)
        }
    }

val Project.isGmsReleaseBuild: Boolean
    get() {
        val requests = gradle.startParameter.taskRequests.joinToString(" ") { it.args.joinToString(" ") }
        val regex = Regex("(assemble|install).+GmsRelease", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(requests)
    }

val Project.isHmsBuild: Boolean
    get() {
        val requests = gradle.startParameter.taskRequests.joinToString(" ") { it.args.joinToString(" ") }
        val regex = Regex("(assemble|install).+Hms", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(requests)
    }