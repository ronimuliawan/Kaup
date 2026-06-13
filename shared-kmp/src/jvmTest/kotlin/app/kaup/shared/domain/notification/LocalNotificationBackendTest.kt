package app.kaup.shared.domain.notification

import app.kaup.shared.models.notification.NotificationEvent
import app.kaup.shared.models.notification.NotificationType
import kotlin.test.Test
import kotlin.test.assertFalse

class LocalNotificationBackendTest {

    private val backend = LocalNotificationBackend()

    @Test
    fun `isRemoteCapable returns false for local backend`() {
        assertFalse(backend.isRemoteCapable())
    }

    @Test
    fun `scheduleLocalAlert prints to console on JVM`() {
        val event = NotificationEvent(
            id = "test_1",
            type = NotificationType.SHIFT_OPEN,
            title = "Test",
            message = "Test Message"
        )
        backend.scheduleLocalAlert(event)
        backend.cancelAlert("test_1")
        // Success is passing without crashing
    }
}
