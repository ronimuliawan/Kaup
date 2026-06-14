package app.kaup.core.network.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.kaup.shared.sync.SyncBackend
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class ProcessingMode {
    STANDALONE, ASSISTED, SERVER_FIRST
}

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncBackend: SyncBackend
) {
    private val workManager = WorkManager.getInstance(context)
    private val syncWorkName = "KaupPeriodicSync"

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            syncWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueue(request)
    }

    suspend fun pushNowServerFirst(): Boolean {
        // Direct suspension call to block local write operations if server fails
        return try {
            syncBackend.pushRecords()
            true
        } catch (e: Exception) {
            false
        }
    }
}
