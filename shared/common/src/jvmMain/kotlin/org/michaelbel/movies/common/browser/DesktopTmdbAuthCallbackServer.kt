package org.michaelbel.movies.common.browser

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

data class DesktopTmdbAuthCallback(
    val requestToken: String,
    val approved: Boolean
)

object DesktopTmdbAuthCallbackServer {

    private const val callbackPath = "/redirect_url"

    private val callbackFlowInternal = MutableSharedFlow<DesktopTmdbAuthCallback>(extraBufferCapacity = 1)

    val callbackFlow = callbackFlowInternal.asSharedFlow()

    private var server: HttpServer? = null
    private var currentRedirectUrl: String = defaultTmdbAuthRedirectUrl

    fun redirectUrl(): String {
        startIfNeeded()
        return currentRedirectUrl
    }

    private fun startIfNeeded() {
        if (server != null) {
            return
        }

        synchronized(this) {
            if (server != null) {
                return
            }

            val httpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
            httpServer.createContext(callbackPath) { exchange ->
                handleCallback(exchange)
            }
            httpServer.executor = Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "movies-tmdb-auth-callback").apply {
                    isDaemon = true
                }
            }
            httpServer.start()

            server = httpServer
            currentRedirectUrl = "http://127.0.0.1:${httpServer.address.port}$callbackPath"
        }
    }

    private fun handleCallback(
        exchange: HttpExchange
    ) {
        val params = parseQuery(exchange.requestURI.rawQuery.orEmpty())
        val requestToken = params["request_token"]?.takeIf(String::isNotBlank)
        val approved = when (params["approved"]?.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> null
        }

        if (requestToken != null && approved != null) {
            callbackFlowInternal.tryEmit(
                DesktopTmdbAuthCallback(
                    requestToken = requestToken,
                    approved = approved
                )
            )
        }

        val responseBody = """
            <html>
            <head>
            <meta charset="utf-8" />
            <title>Movies</title>
            </head>
            <body style="font-family: sans-serif; padding: 24px;">
            <h2>Movies</h2>
            <p>Login completed. You can close this tab and return to the app.</p>
            </body>
            </html>
        """.trimIndent().toByteArray(StandardCharsets.UTF_8)

        exchange.responseHeaders.add("Content-Type", "text/html; charset=utf-8")
        exchange.sendResponseHeaders(200, responseBody.size.toLong())
        exchange.responseBody.use { output ->
            output.write(responseBody)
        }
    }

    private fun parseQuery(
        query: String
    ): Map<String, String> {
        if (query.isEmpty()) {
            return emptyMap()
        }

        return query
            .split("&")
            .mapNotNull { part ->
                val separatorIndex = part.indexOf('=')
                when {
                    separatorIndex == -1 -> null
                    else -> {
                        val key = part.substring(0, separatorIndex)
                        val value = part.substring(separatorIndex + 1)
                        URLDecoder.decode(key, StandardCharsets.UTF_8) to URLDecoder.decode(value, StandardCharsets.UTF_8)
                    }
                }
            }
            .toMap()
    }
}
