package app.kaup.shared.domain.notification

import app.kaup.shared.models.notification.NotificationEvent

class LocalNotificationBackend : NotificationBackend {
    override fun scheduleLocalAlert(event: NotificationEvent) {
        println("[JVM] Scheduled Notification: ${event.title} - ${event.message} at ${event.targetTime ?: "NOW"}")
    }

    override fun cancelAlert(eventId: String) {
        println("[JVM] Canceled Notification: $eventId")
    }

    override fun isRemoteCapable(): Boolean = false
}
