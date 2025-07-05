@file:OptIn(ExperimentalTime::class)

package org.michaelbel.movies.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.michaelbel.movies.common.ktx.isTimePasses
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.persistence.database.ktx.isEmpty
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AccountUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val interactor: Interactor
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val accountId = interactor.accountId()
            if (isTmdbApiKeyEmpty || accountId.isEmpty) return Result.success()

            val accountExpireTime = interactor.accountExpireTime()
            val currentTime = Clock.System.now().toEpochMilliseconds()
            if (isTimePasses(ONE_DAY_MILLS, accountExpireTime, currentTime)) {
                interactor.accountDetails()
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private companion object {
        private val ONE_DAY_MILLS = TimeUnit.DAYS.toMillis(1)
    }
}