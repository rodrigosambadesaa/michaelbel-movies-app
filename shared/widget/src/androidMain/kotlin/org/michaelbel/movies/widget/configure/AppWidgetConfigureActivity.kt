package org.michaelbel.movies.widget.configure

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class AppWidgetConfigureActivity: ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContent {
            AppWidgetConfigureActivityContent(
                onBackClick = ::finish,
                enableEdgeToEdge = { statusBarStyle, navigationBarStyle ->
                    enableEdgeToEdge(statusBarStyle as SystemBarStyle, navigationBarStyle as SystemBarStyle)
                }
            )
        }
        appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }
}
