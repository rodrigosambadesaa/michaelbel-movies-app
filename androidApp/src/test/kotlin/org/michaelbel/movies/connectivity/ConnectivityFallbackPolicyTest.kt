package org.michaelbel.movies.connectivity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.michaelbel.movies.network.connectivity.ConnectivityFallbackPolicy
import org.michaelbel.movies.network.connectivity.ConnectivityTierResult
import org.michaelbel.movies.network.connectivity.MoviesConnectivityTargets

class ConnectivityFallbackPolicyTest {
    private val policy = ConnectivityFallbackPolicy()

    @Test
    fun generalProbeIsSkippedWhenAnyMoviesEndpointIsReachable() {
        var generalCalls = 0

        val result = policy.diagnose(
            applicationProbe = {
                ConnectivityTierResult(
                    reachable = true,
                    reachedEndpoint = "https://api.themoviedb.org/3/",
                    attemptedEndpoints = MoviesConnectivityTargets.backendProbeUrls
                )
            },
            generalProbe = {
                generalCalls++
                ConnectivityTierResult(true, "https://www.google.com/generate_204", emptyList())
            }
        )

        assertEquals(0, generalCalls)
        assertTrue(result.applicationTier.reachable)
        assertFalse(result.usedGeneralFallback)
        assertNull(result.generalTier)
    }

    @Test
    fun generalProbeRunsOnlyAfterCompleteMoviesTierFails() {
        var generalCalls = 0

        val result = policy.diagnose(
            applicationProbe = {
                ConnectivityTierResult(
                    reachable = false,
                    reachedEndpoint = null,
                    attemptedEndpoints = MoviesConnectivityTargets.backendProbeUrls
                )
            },
            generalProbe = {
                generalCalls++
                ConnectivityTierResult(
                    reachable = true,
                    reachedEndpoint = "dns://system/example.com",
                    attemptedEndpoints = listOf("dns://system/example.com")
                )
            }
        )

        assertEquals(1, generalCalls)
        assertFalse(result.applicationTier.reachable)
        assertTrue(result.usedGeneralFallback)
        assertTrue(result.generalTier?.reachable == true)
        assertTrue(result.reachable)
    }

    @Test
    fun moviesTierContainsOnlyDestinationsUsedByTheApp() {
        assertEquals(
            listOf(
                "api.themoviedb.org",
                "image.tmdb.org",
                "themoviedb.org",
                "www.themoviedb.org",
                "www.gravatar.com"
            ),
            MoviesConnectivityTargets.backendDomains
        )

        assertTrue(
            MoviesConnectivityTargets.backendProbeUrls.all { url ->
                MoviesConnectivityTargets.backendDomains.any { domain -> url.contains(domain) }
            }
        )
        assertFalse(
            MoviesConnectivityTargets.backendProbeUrls.any { url ->
                url.contains("google.com") ||
                    url.contains("facebook.com") ||
                    url.contains("apple.com") ||
                    url.contains("amazon.com") ||
                    url.contains("wolframalpha.com")
            }
        )
    }
}
