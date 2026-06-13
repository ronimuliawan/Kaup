package app.kaup.shared.domain.sync

import app.kaup.shared.models.sync.PendingRecord
import app.kaup.shared.models.sync.RemoteUpdate
import app.kaup.shared.models.sync.SyncResult
import kotlinx.datetime.Instant

interface SyncBackend {
    suspend fun pushRecords(records: List<PendingRecord>): SyncResult
    suspend fun pullUpdates(since: Instant): List<RemoteUpdate>
    suspend fun uploadFile(localPath: String): String
    fun isConfigured(): Boolean
}
