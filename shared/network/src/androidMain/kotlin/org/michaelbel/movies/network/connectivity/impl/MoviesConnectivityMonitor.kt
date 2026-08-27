package org.michaelbel.movies.network.connectivity.impl

import android.content.Context
import android.net.ConnectivityManager
import android.os.SystemClock
import android.util.Log
import java.io.Closeable
import java.net.InetAddress
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.i2p.android.router.util.ConnectivityAndInternetAccess
import org.michaelbel.movies.network.connectivity.ConnectivityFallbackPolicy
import org.michaelbel.movies.network.connectivity.ConnectivityFallbackResult
import org.michaelbel.movies.network.connectivity.ConnectivityTierResult
import org.michaelbel.movies.network.connectivity.MoviesConnectivityTargets

private const val TAG = "MoviesConnectivity"
private const val DIAGNOSTIC_COOLDOWN_MS = 5_000L
private const val BACKEND_DNS_DEADLINE_MS = 900L

data class BackendDnsResult(
    val domain: String,
    val resolved: Boolean,
    val addresses: List<String>
)

data class MoviesConnectivityDiagnostic(
    val failedBackendHost: String,
    val passiveState: ConnectivityAndInternetAccess.NetworkState,
    val backendDns: List<BackendDnsResult>,
    val fallbackResult: ConnectivityFallbackResult?,
    val elapsedMilliseconds: Long
)

/**
 * Application-level network observer and failure diagnostic.
 *
 * Normal operation is passive: one observer follows Android's actual default network and
 * generates no DNS or HTTP traffic. Active diagnostics are started only after OkHttp/Ktor
 * reports a transport IOException. The real application request is never gated or replaced.
 *
 * Diagnostic order after such a failure:
 *  1. Resolve only domains used by Movies on the active Android Network.
 *  2. Probe only Movies/TMDb/Gravatar HTTPS destinations.
 *  3. Only if every application destination fails, run the generic DNS/HTTPS fallback from
 *     ConnectivityAndInternetAccess.
 */
class MoviesConnectivityMonitor(
    context: Context
) : Closeable {
    private val applicationContext = context.applicationContext ?: context
    private val connectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkStateMutable = MutableStateFlow(
        ConnectivityAndInternetAccess.snapshotNetworkState(applicationContext)
    )
    val networkState: StateFlow<ConnectivityAndInternetAccess.NetworkState> =
        networkStateMutable.asStateFlow()

    private val lastDiagnosticMutable = MutableStateFlow<MoviesConnectivityDiagnostic?>(null)
    val lastDiagnostic: StateFlow<MoviesConnectivityDiagnostic?> =
        lastDiagnosticMutable.asStateFlow()

    private val fallbackPolicy = ConnectivityFallbackPolicy()

    // App-specific tier deliberately disables the gist's generic DNS phase. DNS for the app's
    // own domains is collected separately immediately before these HTTPS probes.
    private val applicationConnectivity = ConnectivityAndInternetAccess.Builder()
        .setDnsResolvers(emptyList())
        .setHosts(MoviesConnectivityTargets.backendProbeUrls)
        .build()

    // Untouched gist defaults: effective/system DNS -> public DNS -> generic HTTPS hosts.
    private val genericConnectivity = ConnectivityAndInternetAccess.Builder().build()

    private val diagnosticExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "movies-connectivity-diagnostic").apply { isDaemon = true }
    }
    private val diagnosticInFlight = AtomicBoolean(false)
    private val lastDiagnosticStartedAt = AtomicLong(Long.MIN_VALUE)
    private val closed = AtomicBoolean(false)

    private val observer = ConnectivityAndInternetAccess.observeNetwork(applicationContext) { state ->
        networkStateMutable.value = state
        Log.d(
            TAG,
            "default-network connected=${state.connected}, " +
                "validated=${state.internetValidated}, " +
                "captivePortal=${state.captivePortalDetected}"
        )
    }

    fun onBackendTransportFailure(failedHost: String, failure: Throwable) {
        if (closed.get()) return

        val now = SystemClock.elapsedRealtime()
        val previous = lastDiagnosticStartedAt.get()
        if (previous != Long.MIN_VALUE && now - previous < DIAGNOSTIC_COOLDOWN_MS) {
            Log.d(TAG, "diagnostic skipped (cooldown), backend=$failedHost")
            return
        }
        if (!diagnosticInFlight.compareAndSet(false, true)) {
            Log.d(TAG, "diagnostic skipped (already running), backend=$failedHost")
            return
        }
        lastDiagnosticStartedAt.set(now)

        Log.w(
            TAG,
            "backend transport failure host=$failedHost, " +
                "type=${failure.javaClass.simpleName}; starting app-first diagnosis"
        )

        diagnosticExecutor.execute {
            try {
                runDiagnostic(failedHost)
            } catch (runtime: RuntimeException) {
                Log.e(TAG, "connectivity diagnostic failed", runtime)
            } finally {
                diagnosticInFlight.set(false)
            }
        }
    }

    private fun runDiagnostic(failedHost: String) {
        val started = SystemClock.elapsedRealtime()
        val passive = networkStateMutable.value

        if (!passive.connected) {
            val diagnostic = MoviesConnectivityDiagnostic(
                failedBackendHost = failedHost,
                passiveState = passive,
                backendDns = emptyList(),
                fallbackResult = null,
                elapsedMilliseconds = SystemClock.elapsedRealtime() - started
            )
            lastDiagnosticMutable.value = diagnostic
            Log.w(TAG, "diagnosis: Android default network is offline; active probes skipped")
            return
        }

        val backendDns = resolveBackendDomains()
        Log.d(
            TAG,
            "backend DNS: " + backendDns.joinToString { result ->
                "${result.domain}=${if (result.resolved) "ok" else "failed"}"
            }
        )

        val fallbackResult = fallbackPolicy.diagnose(
            applicationProbe = {
                applicationConnectivity.checkInternetBlocking(applicationContext).toTierResult()
            },
            generalProbe = {
                // This lambda is not invoked unless the complete application tier failed.
                genericConnectivity.checkInternetBlocking(applicationContext).toTierResult()
            }
        )

        val diagnostic = MoviesConnectivityDiagnostic(
            failedBackendHost = failedHost,
            passiveState = passive,
            backendDns = backendDns,
            fallbackResult = fallbackResult,
            elapsedMilliseconds = SystemClock.elapsedRealtime() - started
        )
        lastDiagnosticMutable.value = diagnostic

        when {
            fallbackResult.applicationTier.reachable -> Log.w(
                TAG,
                "diagnosis: Movies endpoints are reachable via " +
                    "${fallbackResult.applicationTier.reachedEndpoint}; original request failure " +
                    "was transient or request-specific"
            )

            fallbackResult.generalTier?.reachable == true -> Log.w(
                TAG,
                "diagnosis: generic Internet works via " +
                    "${fallbackResult.generalTier.reachedEndpoint}, but every Movies endpoint " +
                    "probe failed"
            )

            else -> Log.w(
                TAG,
                "diagnosis: Movies endpoints failed and generic Internet fallback also failed"
            )
        }
    }

    private fun resolveBackendDomains(): List<BackendDnsResult> {
        val network = connectivityManager.activeNetwork
        if (network == null) {
            return MoviesConnectivityTargets.backendDomains.map { domain ->
                BackendDnsResult(domain, false, emptyList())
            }
        }

        val dnsExecutor = Executors.newFixedThreadPool(
            MoviesConnectivityTargets.backendDomains.size.coerceAtLeast(1)
        ) { runnable ->
            Thread(runnable, "movies-backend-dns").apply { isDaemon = true }
        }
        val deadline = SystemClock.elapsedRealtime() + BACKEND_DNS_DEADLINE_MS

        try {
            val futures = MoviesConnectivityTargets.backendDomains.associateWith { domain ->
                dnsExecutor.submit(
                    Callable {
                        try {
                            val addresses: Array<InetAddress> = network.getAllByName(domain)
                            BackendDnsResult(
                                domain = domain,
                                resolved = addresses.isNotEmpty(),
                                addresses = addresses.map(InetAddress::getHostAddress)
                            )
                        } catch (_: Exception) {
                            BackendDnsResult(domain, false, emptyList())
                        }
                    }
                )
            }

            return MoviesConnectivityTargets.backendDomains.map { domain ->
                val remaining = deadline - SystemClock.elapsedRealtime()
                if (remaining <= 0L) {
                    futures.getValue(domain).cancel(true)
                    BackendDnsResult(domain, false, emptyList())
                } else {
                    try {
                        futures.getValue(domain).get(remaining, TimeUnit.MILLISECONDS)
                    } catch (_: TimeoutException) {
                        futures.getValue(domain).cancel(true)
                        BackendDnsResult(domain, false, emptyList())
                    } catch (_: Exception) {
                        BackendDnsResult(domain, false, emptyList())
                    }
                }
            }
        } finally {
            dnsExecutor.shutdownNow()
        }
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        observer.close()
        diagnosticExecutor.shutdownNow()
    }

    private fun ConnectivityAndInternetAccess.InternetResult.toTierResult() =
        ConnectivityTierResult(
            reachable = reachable,
            reachedEndpoint = reachedHost,
            attemptedEndpoints = attemptedHosts
        )
}
