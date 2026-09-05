# Network Connectivity Monitoring & Diagnostics

This module integrates connectivity observation and failure diagnostics based on [`ConnectivityAndInternetAccess.kt`](https://gist.github.com/rodrigosambadesaa/729cca29a031fef4e2f15751863b655f) into the Movies networking stack.

## Architecture & Design

Normal operation performs **no connectivity pre-flight** before backend requests:

1. **Passive observation**: An application-level `NetworkObserver` follows Android's default network state passively without sending any network traffic.
2. **Transparent execution**: Ktor/OkHttp executes normal TMDb API requests directly.
3. **Failure-triggered active diagnostics**: Active network diagnosis is initiated **only when** a transport `IOException` (such as a connection failure or DNS error) is encountered.
4. **App-first domain probes**:
   - First, the diagnostic resolves the specific domains used by Movies:
     - `api.themoviedb.org`
     - `image.tmdb.org`
     - `themoviedb.org`
     - `www.themoviedb.org`
     - `www.gravatar.com`
   - Next, HTTPS reachability probes are run against the application endpoints.
5. **Generic fallback**: Only if all application-specific probes fail does the generic fallback run (checking system DNS, public DNS resolvers, and general HTTPS probe targets).

HTTP response errors (such as 401, 404, or 500) are valid HTTP responses and do not trigger network diagnostics. The interceptor always rethrows the original transport exception unchanged.

## Testing & Logcat Inspection

Filter Logcat by tag:

```text
MoviesConnectivity
```

### Verification Scenarios

1. **Normal network connection**: Browse and search movies. The passive observer reports default network changes; no active diagnostics run.
2. **Network switching**: Switch between Wi-Fi and Cellular. The observer emits updated default-network states without active polling.
3. **No network connection**: When all network interfaces are disabled, `connected=false` is reported. If a backend request fails, active probes are skipped because the network is known to be offline.
4. **Domain-specific reachability failure**: If TMDb endpoints are blocked while general Internet works, app-domain DNS/HTTPS probes fail first, and generic fallback reports general Internet availability.
5. **Active Diagnostic Guards**: Active diagnostics implement a 5-second cooldown and single-flight execution to prevent probe spam during burst request failures.
