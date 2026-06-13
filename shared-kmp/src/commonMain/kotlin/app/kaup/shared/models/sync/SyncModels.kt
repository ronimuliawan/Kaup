package app.kaup.shared.models.sync

import kotlinx.datetime.Instant

enum class SyncOperation { INSERT, UPDATE, DELETE }

data class PendingRecord(
    val id: String,
    val entityType: String,
    val operation: SyncOperation,
    val payload: String,
    val timestamp: Instant
)

data class RemoteUpdate(
    val id: String,
    val entityType: String,
    val operation: SyncOperation,
    val payload: String,
    val timestamp: Instant
)

sealed class SyncResult {
    data object Success : SyncResult()
    data class PartialSuccess(val failedIds: List<String>) : SyncResult()
    data class Failure(val reason: String) : SyncResult()
}
