package org.michaelbel.movies.persistence.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import org.koin.dsl.module
import org.michaelbel.movies.persistence.datastore.DATA_STORE_NAME
import java.io.File

actual val dataStoreKoinModule = module {
    single<DataStore<Preferences>> {
        val dataStoreDir = File(System.getProperty("java.io.tmpdir"), "movies")
        dataStoreDir.mkdirs()

        createDataStore(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            producePath = { dataStoreDir.resolve(DATA_STORE_NAME).absolutePath }
        )
    }
}
