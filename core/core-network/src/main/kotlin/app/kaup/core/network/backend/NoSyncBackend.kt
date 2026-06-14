package app.kaup.core.network.backend

import app.kaup.shared.sync.SyncBackend
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoSyncBackend @Inject constructor() : SyncBackend {
    override suspend fun pushRecords() {
        // No-op for Tier 0
    }

    override suspend fun pullUpdates() {
        // No-op for Tier 0
    }
}
