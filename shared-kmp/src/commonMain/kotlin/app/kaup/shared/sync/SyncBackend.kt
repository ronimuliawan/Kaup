package app.kaup.shared.sync

interface SyncBackend {
    suspend fun pushRecords()
    suspend fun pullUpdates()
}
