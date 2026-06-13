package app.kaup.shared.sync

interface NotificationBackend {
    suspend fun fireNotification(title: String, message: String)
}
