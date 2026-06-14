package app.kaup.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import app.kaup.core.data.dao.ItemDao
import app.kaup.core.data.dao.LocationDao
import app.kaup.core.data.dao.StockMovementDao
import app.kaup.core.data.entities.ItemEntity
import app.kaup.core.data.entities.LocationEntity
import app.kaup.core.data.entities.StockMovementEntity

import app.kaup.core.data.dao.UserDao
import app.kaup.core.data.entities.UserEntity
import app.kaup.core.data.converters.RoleConverter
import androidx.room.TypeConverters

@TypeConverters(RoleConverter::class)
@Database(
    entities = [
        LocationEntity::class,
        ItemEntity::class,
        StockMovementEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class KaupDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun itemDao(): ItemDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun userDao(): UserDao
    
    // Note: Database builder with `fallbackToDestructiveMigration()` 
    // per ADR-018 will be handled in the `:android-app` DI module.
}
