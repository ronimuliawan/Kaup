package app.kaup.core.network.notifications

import app.kaup.shared.sync.NotificationBackend
import javax.inject.Inject
import javax.inject.Singleton

enum class AppEvent {
    LOW_STOCK, SYNC_FAILURE, SHIFT_OPEN_REMINDER, BACKUP_REMINDER
}

@Singleton
class NotificationRouter @Inject constructor(
    private val backend: NotificationBackend
) {
    suspend fun routeEvent(event: AppEvent, message: String) {
        val title = when (event) {
            AppEvent.LOW_STOCK -> "Low Stock Alert"
            AppEvent.SYNC_FAILURE -> "Sync Failed"
            AppEvent.SHIFT_OPEN_REMINDER -> "Shift Reminder"
            AppEvent.BACKUP_REMINDER -> "Backup Required"
        }
        backend.fireNotification(title, message)
    }
}
