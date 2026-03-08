package org.michaelbel.movies.persistence.database.di

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import org.michaelbel.movies.persistence.database.db.AppDatabase
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
internal fun createDocumentsDriver(): SQLiteDriver {
    return DocumentsSQLiteDriver(
        directory = documentsDirectoryPath(),
        delegate = BundledSQLiteDriver()
    )
}

internal fun documentDatabasePath(): String {
    return "${documentsDirectoryPath()}/${AppDatabase.DATABASE_NAME}"
}

@OptIn(ExperimentalForeignApi::class)
private class DocumentsSQLiteDriver(
    private val directory: String,
    private val delegate: SQLiteDriver
) : SQLiteDriver {

    init {
        ensureDirectoryExists(directory)
    }

    override val hasConnectionPool: Boolean
        get() = delegate.hasConnectionPool

    override fun open(fileName: String): SQLiteConnection {
        return delegate.open(resolveFileName(fileName))
    }

    private fun resolveFileName(fileName: String): String {
        if (fileName == ":memory:") return fileName
        if (fileName.startsWith("/")) return fileName
        return "$directory/$fileName"
    }

    private fun ensureDirectoryExists(path: String) {
        val manager = NSFileManager.defaultManager
        if (!manager.fileExistsAtPath(path)) {
            manager.createDirectoryAtPath(path, true, null, null)
        }
    }
}

private fun documentsDirectoryPath(): String {
    val paths = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
    val path = (paths.firstOrNull() as? NSURL)?.path
    return path ?: "${NSHomeDirectory()}/Documents"
}
