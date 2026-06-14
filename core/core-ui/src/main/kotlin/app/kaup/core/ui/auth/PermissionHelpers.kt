package app.kaup.core.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import app.kaup.shared.domain.models.auth.Permission

val LocalPermissions = staticCompositionLocalOf<Set<Permission>> { emptySet() }

@Composable
fun hasPermission(permission: Permission): Boolean {
    return LocalPermissions.current.contains(permission)
}

@Composable
fun hasAnyPermission(vararg permissions: Permission): Boolean {
    val current = LocalPermissions.current
    return permissions.any { current.contains(it) }
}

@Composable
fun hasAllPermissions(vararg permissions: Permission): Boolean {
    val current = LocalPermissions.current
    return permissions.all { current.contains(it) }
}

@Composable
fun RequirePermission(permission: Permission, content: @Composable () -> Unit) {
    if (hasPermission(permission)) {
        content()
    }
}
