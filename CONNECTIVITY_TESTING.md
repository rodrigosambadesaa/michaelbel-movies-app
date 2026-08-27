# Connectivity diagnostics test guide

This branch integrates Rodrigo Sambade Saá's `ConnectivityAndInternetAccess.kt` gist and adapts it to the Movies networking stack.

Source gist: <https://gist.github.com/rodrigosambadesaa/729cca29a031fef4e2f15751863b655f>

## Runtime design

Normal operation performs **no connectivity pre-flight** before TMDb requests.

1. One application-level `NetworkObserver` passively follows Android's actual default network.
2. Ktor/OkHttp makes the real request to the Movies backend.
3. Only an `IOException` (transport failure without an HTTP response) triggers active diagnosis.
4. Diagnosis resolves the app's own domains first:
   - `api.themoviedb.org`
   - `image.tmdb.org`
   - `themoviedb.org`
   - `www.themoviedb.org`
   - `www.gravatar.com`
5. The same application destinations are then probed over HTTPS.
6. **Only if every application HTTPS probe fails** is the gist's generic fallback run: effective/system DNS, bounded public DNS fallback, then its generic HTTPS hosts.

HTTP errors such as 401, 404 or 500 are real HTTP responses and therefore do not trigger an Internet diagnostic. The interceptor always rethrows the original transport exception unchanged.

## Android Studio

The project requires JDK 21. Open the repository root in Android Studio and use the existing Gradle configuration.

To make authenticated TMDb requests, put your own API key in the untracked root `local.properties`:

```properties
TMDB_API_KEY=your_own_tmdb_api_key
```

Alternatively supply `TMDB_API_KEY` as a Gradle property or environment variable. Never commit the key.

Recommended local build for this validation:

```bash
./gradlew detekt testFossDebugUnitTest androidApp:assembleFossDebug
```

The FOSS debug APK is signed with the repository's existing debug key and can be installed on a physical Android device.

## Real-device validation

Filter Logcat by tag:

```text
MoviesConnectivity
```

Suggested checks:

1. **Normal Wi-Fi/mobile connection:** browse/search movies. The passive observer should report the default network; no active diagnostic should run.
2. **Switch Wi-Fi to mobile data:** the observer should emit the new default-network state without polling.
3. **Disconnect all network access:** the observer should report `connected=false`. A subsequent backend transport failure should skip active probes because Android already reports the default network offline.
4. **Break only TMDb reachability while general Internet still works** (for example with a controlled DNS/firewall test): app-domain DNS/HTTPS probes run first. Generic fallback runs only after the complete Movies HTTPS tier fails and should report that general Internet still works.
5. **Break DNS/general Internet:** Movies DNS/HTTPS probes fail first, then the gist's generic DNS/HTTPS fallback runs and should also fail.
6. **Captive portal:** Logcat exposes Android's passive `captivePortalDetected` and `internetValidated` signals independently from active reachability.

The active diagnostic has a five-second cooldown and single-flight guard so a burst of failed requests does not generate a burst of probes.

## CI prerelease

`.github/workflows/connectivity-test-release.yml` runs Detekt, unit tests, builds `FossDebug`, uploads the APK as an Actions artifact, and creates a GitHub prerelease for each push to `feature/connectivity-diagnostics`.

For a prerelease that can actually load TMDb content, configure a repository Actions secret named `TMDB_API_KEY`. If it is absent, CI still proves that the project compiles and produces an installable APK, but TMDb requests in that APK will receive authentication errors.
