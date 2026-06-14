package app.kaup.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaup.core.data.auth.SessionManager
import app.kaup.core.data.dao.UserDao
import app.kaup.core.data.entities.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    val users: StateFlow<List<UserEntity>> = userDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun login(user: UserEntity) {
        sessionManager.login(user)
    }
}
