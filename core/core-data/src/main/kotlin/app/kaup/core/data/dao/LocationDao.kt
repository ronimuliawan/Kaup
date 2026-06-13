package app.kaup.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.kaup.core.data.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: LocationEntity): Long

    @Query("SELECT * FROM locations WHERE isDefault = 1 LIMIT 1")
    fun getDefaultLocation(): Flow<LocationEntity?>
}
