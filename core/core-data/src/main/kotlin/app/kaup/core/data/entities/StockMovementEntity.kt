package app.kaup.core.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_movements",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId"), Index("itemId")]
)
data class StockMovementEntity(
    @PrimaryKey val id: String,
    val locationId: String,
    val itemId: String,
    val quantity: Double,
    val type: String, // e.g. "SALE", "RECEIPT", "ADJUSTMENT"
    val timestamp: Long,
    val deviceId: String
)
