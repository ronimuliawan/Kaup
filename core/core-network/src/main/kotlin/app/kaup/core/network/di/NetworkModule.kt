package app.kaup.core.network.di

import app.kaup.core.network.backend.NoSyncBackend
import app.kaup.core.network.notifications.LocalNotificationBackend
import app.kaup.shared.sync.NotificationBackend
import app.kaup.shared.sync.SyncBackend
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindSyncBackend(
        noSyncBackend: NoSyncBackend
    ): SyncBackend

    @Binds
    abstract fun bindNotificationBackend(
        localNotificationBackend: LocalNotificationBackend
    ): NotificationBackend
}
