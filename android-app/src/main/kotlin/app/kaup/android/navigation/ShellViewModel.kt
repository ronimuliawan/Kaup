package app.kaup.android.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaup.core.data.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShellViewModel @Inject constructor(
    userDao: UserDao
) : ViewModel() {

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
}
