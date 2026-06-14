package app.kaup.core.data.auth

import app.kaup.core.data.entities.UserEntity
import app.kaup.shared.domain.models.auth.Permission
import app.kaup.shared.domain.models.auth.getDefaultPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _permissions = MutableStateFlow<Set<Permission>>(emptySet())
    val permissions: StateFlow<Set<Permission>> = _permissions.asStateFlow()

    fun login(user: UserEntity) {
        _currentUser.value = user
        _permissions.value = user.role.getDefaultPermissions()
    }

    fun logout() {
        _currentUser.value = null
        _permissions.value = emptySet()
    }

    fun hasPermission(permission: Permission): Boolean {
        return _permissions.value.contains(permission)
    }
}
