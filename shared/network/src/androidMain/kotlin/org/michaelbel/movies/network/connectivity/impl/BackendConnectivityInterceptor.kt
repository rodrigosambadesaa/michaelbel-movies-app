package org.michaelbel.movies.network.connectivity.impl

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response
import org.michaelbel.movies.network.connectivity.MoviesConnectivityTargets

/**
 * Observes transport failures from the real backend request. It never performs a
 * pre-flight check and never changes the response/error seen by Ktor.
 */
class BackendConnectivityInterceptor(
    private val connectivityMonitor: MoviesConnectivityMonitor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (failure: IOException) {
            if (request.url.host in MoviesConnectivityTargets.backendDomains) {
                connectivityMonitor.onBackendTransportFailure(request.url.host, failure)
            }
            throw failure
        }
    }
}
