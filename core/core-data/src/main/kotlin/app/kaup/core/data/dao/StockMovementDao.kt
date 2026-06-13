package app.kaup.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.kaup.core.data.entities.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movement: StockMovementEntity): Long

    @Query("SELECT * FROM stock_movements WHERE locationId = :locationId AND itemId = :itemId ORDER BY timestamp ASC")
    fun getMovementsForItem(locationId: String, itemId: String): Flow<List<StockMovementEntity>>
}
