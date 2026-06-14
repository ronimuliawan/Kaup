package app.kaup.android.di

import android.content.Context
import androidx.room.Room
import app.kaup.core.data.KaupDatabase
import app.kaup.core.data.dao.ItemDao
import app.kaup.core.data.dao.LocationDao
import app.kaup.core.data.dao.StockMovementDao
import app.kaup.core.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): KaupDatabase {
        return Room.databaseBuilder(
            context,
            KaupDatabase::class.java,
            "kaup_database"
        )
        // ADR-018: Destructive migration during alpha phase
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideLocationDao(database: KaupDatabase): LocationDao = database.locationDao()

    @Provides
    fun provideItemDao(database: KaupDatabase): ItemDao = database.itemDao()

    @Provides
    fun provideStockMovementDao(database: KaupDatabase): StockMovementDao = database.stockMovementDao()

    @Provides
    fun provideUserDao(database: KaupDatabase): UserDao = database.userDao()
}
