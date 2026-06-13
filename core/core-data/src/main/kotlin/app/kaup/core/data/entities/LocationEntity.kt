package app.kaup.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String?,
    val isDefault: Boolean = true,
    val syncStatus: String = "PENDING"
)
