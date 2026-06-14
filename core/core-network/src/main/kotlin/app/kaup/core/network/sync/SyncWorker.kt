package app.kaup.core.network.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.kaup.shared.sync.SyncBackend
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncBackend: SyncBackend
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Note: In a real flow, we query sync_queue via core-data DAOs.
            syncBackend.pushRecords()
            syncBackend.pullUpdates()
            Result.success()
        } catch (e: Exception) {
            // Return retry to trigger WorkManager's exponential backoff policy
            Result.retry()
        }
    }
}
