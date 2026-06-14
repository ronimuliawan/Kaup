package app.kaup.core.data.converters

import androidx.room.TypeConverter
import app.kaup.shared.domain.models.auth.Role

class RoleConverter {
    @TypeConverter
    fun toRole(value: String): Role = enumValueOf<Role>(value)

    @TypeConverter
    fun fromRole(value: Role): String = value.name
}
