package org.michaelbel.movies.network.connectivity

/**
 * Network destinations actually used by Movies. Diagnostics always try these first.
 * Generic Internet destinations from ConnectivityAndInternetAccess are deliberately
 * reserved for fallback diagnosis after this complete application tier has failed.
 */
object MoviesConnectivityTargets {
    val backendDomains: List<String> = listOf(
        "api.themoviedb.org",
        "image.tmdb.org",
        "themoviedb.org",
        "www.themoviedb.org",
        "www.gravatar.com"
    )

    val backendProbeUrls: List<String> = listOf(
        "https://api.themoviedb.org/3/",
        "https://image.tmdb.org/",
        "https://themoviedb.org/",
        "https://www.themoviedb.org/",
        "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=404"
    )
}

data class ConnectivityTierResult(
    val reachable: Boolean,
    val reachedEndpoint: String?,
    val attemptedEndpoints: List<String>
)

data class ConnectivityFallbackResult(
    val applicationTier: ConnectivityTierResult,
    val generalTier: ConnectivityTierResult?
) {
    val reachable: Boolean
        get() = applicationTier.reachable || generalTier?.reachable == true

    val usedGeneralFallback: Boolean
        get() = generalTier != null
}

/**
 * Encodes the application-first invariant independently of Android and networking APIs,
 * so the fallback order can be unit-tested without performing network I/O.
 */
class ConnectivityFallbackPolicy {
    fun diagnose(
        applicationProbe: () -> ConnectivityTierResult,
        generalProbe: () -> ConnectivityTierResult
    ): ConnectivityFallbackResult {
        val application = applicationProbe()
        if (application.reachable) {
            return ConnectivityFallbackResult(
                applicationTier = application,
                generalTier = null
            )
        }

        return ConnectivityFallbackResult(
            applicationTier = application,
            generalTier = generalProbe()
        )
    }
}
