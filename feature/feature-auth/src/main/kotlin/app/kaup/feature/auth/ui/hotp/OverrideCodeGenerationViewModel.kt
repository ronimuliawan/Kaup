package app.kaup.feature.auth.ui.hotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaup.core.data.auth.SessionManager
import app.kaup.core.data.crypto.KeystoreManager
import app.kaup.core.data.dao.UserDao
import app.kaup.shared.domain.HOTPGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OverrideCodeState(
    val currentCode: String? = null,
    val error: String? = null,
    val isGenerating: Boolean = false
)

@HiltViewModel
class OverrideCodeGenerationViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val keystoreManager: KeystoreManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverrideCodeState())
    val uiState: StateFlow<OverrideCodeState> = _uiState.asStateFlow()

    init {
        generateCode()
    }

    fun generateCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            
            val user = sessionManager.currentUser.value
            if (user == null || user.hotpSecretEncrypted == null) {
                _uiState.update { 
                    it.copy(
                        isGenerating = false, 
                        error = "HOTP secret not configured for this user. Please complete HOTP Provisioning first."
                    ) 
                }
                return@launch
            }

            try {
                val secretBytes = keystoreManager.decrypt(user.hotpSecretEncrypted!!)
                val counter = user.hotpCounter
                
                val code = HOTPGenerator.generateCode(secretBytes, counter)
                
                val newCounter = counter + 1
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    userDao.updateUserHotpCounter(user.id, newCounter)
                }
                
                _uiState.update { 
                    it.copy(
                        isGenerating = false,
                        currentCode = code,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isGenerating = false,
                        error = "Failed to generate code: ${e.message}"
                    ) 
                }
            }
        }
    }
}
