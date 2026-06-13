package app.kaup.shared.models

import kotlinx.datetime.Instant

enum class MovementType {
    SALE, RECEIVING, TRANSFER, ADJUSTMENT, WASTE
}

enum class MovementDirection {
    IN, OUT
}

enum class SyncStatus {
    PENDING, SYNCING, SYNCED, FAILED, CONFLICT
}

data class StockMovement(
    val id: String,
    val itemId: String,
    val type: MovementType,
    val direction: MovementDirection,
    val quantity: Double,
    val transactionId: String?,
    val deviceId: String,
    val timestamp: Instant,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
