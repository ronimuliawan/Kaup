package app.kaup.core.data.converters

import androidx.room.TypeConverter
import app.kaup.shared.domain.models.auth.Permission

class PermissionSetConverter {
    @TypeConverter
    fun fromString(value: String?): Set<Permission>? {
        if (value == null) return null
        if (value.isBlank()) return emptySet()
        return value.split(",").map { Permission.valueOf(it.trim()) }.toSet()
    }

    @TypeConverter
    fun setToString(permissions: Set<Permission>?): String? {
        if (permissions == null) return null
        return permissions.joinToString(",") { it.name }
    }
}
