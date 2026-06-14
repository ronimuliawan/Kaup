package app.kaup.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.kaup.shared.domain.models.auth.Role

import app.kaup.shared.domain.models.auth.Permission

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: Role,
    val pinHash: String,
    val permissionsOverride: Set<Permission>? = null,
    val hotpSecretEncrypted: String? = null,
    val hotpCounter: Long = 0
)
