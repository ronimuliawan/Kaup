package app.kaup.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.kaup.shared.domain.models.auth.Role

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: Role,
    val pinHash: String
)
