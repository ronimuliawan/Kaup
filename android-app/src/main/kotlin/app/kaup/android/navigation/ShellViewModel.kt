package app.kaup.android.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaup.core.data.auth.SessionManager
import app.kaup.core.data.dao.UserDao
import app.kaup.core.data.entities.UserEntity
import app.kaup.core.data.preferences.StorePreferences
import app.kaup.shared.domain.models.auth.Permission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShellViewModel @Inject constructor(
    userDao: UserDao,
    private val sessionManager: SessionManager,
    private val storePreferences: StorePreferences
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = sessionManager.currentUser
    val permissions: StateFlow<Set<Permission>> = sessionManager.permissions

    private var timeoutJob: Job? = null
    private var currentTimeoutMs: Long = 10_000L

    init {
        viewModelScope.launch {
            storePreferences.autoLockTimeoutMs.collect { ms ->
                currentTimeoutMs = ms
                if (currentUser.value != null) {
                    onUserInteraction()
                }
            }
        }
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    onUserInteraction()
                } else {
                    timeoutJob?.cancel()
                }
            }
        }
    }

    // Emits null initially, then the route string once the DB is queried
    val startDestination: StateFlow<String?> = userDao.getAllUsers()
        .map { users -> 
            if (users.isEmpty()) "onboarding" else "lock_screen" 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onUserInteraction() {
        if (currentUser.value == null) {
            timeoutJob?.cancel()
            return
        }

        timeoutJob?.cancel()
        timeoutJob = viewModelScope.launch {
            delay(currentTimeoutMs)
            sessionManager.logout()
        }
    }
}
