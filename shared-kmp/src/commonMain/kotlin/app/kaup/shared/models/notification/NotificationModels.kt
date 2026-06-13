package app.kaup.shared.models.notification

import kotlinx.datetime.Instant

enum class NotificationType {
    LOW_STOCK, SYNC_FAILURE, SHIFT_OPEN, BACKUP_REMINDER, MANAGER_OVERRIDE
}

data class NotificationEvent(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val targetTime: Instant? = null
)
