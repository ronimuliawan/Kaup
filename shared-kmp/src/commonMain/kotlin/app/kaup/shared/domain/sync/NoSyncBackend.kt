package app.kaup.shared.domain.sync

import app.kaup.shared.models.sync.PendingRecord
import app.kaup.shared.models.sync.RemoteUpdate
import app.kaup.shared.models.sync.SyncResult
import kotlinx.datetime.Instant

/**
 * Tier 0 fallback backend. 
 * Intentionally fails to sync, keeping local records flagged as PENDING 
 * so they are preserved until the user graduates to a Tier 1+ backend.
 */
class NoSyncBackend : SyncBackend {
    override suspend fun pushRecords(records: List<PendingRecord>): SyncResult {
        return SyncResult.Failure("Tier 0 - Local Only mode active")
    }

    override suspend fun pullUpdates(since: Instant): List<RemoteUpdate> {
        return emptyList()
    }

    override suspend fun uploadFile(localPath: String): String {
        return localPath
    }

    override fun isConfigured(): Boolean {
        return false
    }
}
