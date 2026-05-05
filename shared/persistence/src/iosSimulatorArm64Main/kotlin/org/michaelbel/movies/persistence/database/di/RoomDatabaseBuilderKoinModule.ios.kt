package org.michaelbel.movies.persistence.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.dsl.module
import org.michaelbel.movies.persistence.database.db.AppDatabase
import org.michaelbel.movies.persistence.database.db.AppDatabase_Impl

actual val roomDatabaseBuilderModule = module {
    factory<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(
            name = documentDatabasePath(),
            factory = { AppDatabase_Impl() }
        ).setDriver(createDocumentsDriver())
    }
}
