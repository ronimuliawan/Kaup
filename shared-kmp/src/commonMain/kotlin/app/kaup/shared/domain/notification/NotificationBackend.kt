package app.kaup.shared.domain.notification

import app.kaup.shared.models.notification.NotificationEvent

interface NotificationBackend {
    fun scheduleLocalAlert(event: NotificationEvent)
    fun cancelAlert(eventId: String)
    fun isRemoteCapable(): Boolean
}
