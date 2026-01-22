package org.michaelbel.movies.repository.ktx

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

actual val defaultDynamicColorsEnabled: Boolean
    @ChecksSdkIntAtLeast(api = 31) get() = Build.VERSION.SDK_INT >= 31
