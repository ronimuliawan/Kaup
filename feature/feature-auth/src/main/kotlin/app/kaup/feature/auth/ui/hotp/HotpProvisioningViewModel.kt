package app.kaup.feature.auth.ui.hotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaup.core.data.auth.SessionManager
import app.kaup.core.data.crypto.KeystoreManager
import app.kaup.core.data.dao.UserDao
import app.kaup.core.data.preferences.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

data class HotpProvisioningState(
    val isGenerating: Boolean = true,
    val otpAuthUri: String? = null,
    val base32Secret: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class HotpProvisioningViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val keystoreManager: KeystoreManager,
    private val userDao: UserDao,
    private val storePreferences: StorePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotpProvisioningState())
    val uiState: StateFlow<HotpProvisioningState> = _uiState.asStateFlow()

    private var rawSecret: ByteArray? = null

    init {
        generateNewSecret()
    }

    private fun generateNewSecret() {
        viewModelScope.launch {
            val storeName = storePreferences.storeName.first()
            val user = sessionManager.currentUser.value ?: return@launch
            
            val random = SecureRandom()
            val secretBytes = ByteArray(20)
            random.nextBytes(secretBytes)
            rawSecret = secretBytes
            
            val base32Secret = encodeBase32(secretBytes)
            val uri = "otpauth://hotp/${storeName}:${user.name}?secret=$base32Secret&issuer=${storeName}&counter=0"
            
            _uiState.update { 
                it.copy(
                    isGenerating = false,
                    otpAuthUri = uri,
                    base32Secret = base32Secret
                )
            }
        }
    }

    fun saveAndComplete() {
        val secret = rawSecret ?: return
        val user = sessionManager.currentUser.value ?: return
        
        viewModelScope.launch {
            val encrypted = keystoreManager.encrypt(secret)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                userDao.updateUserHotp(user.id, encrypted, 0L)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
    
    // Simple Base32 encoder for the OTP URI
    private fun encodeBase32(bytes: ByteArray): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        var i = 0
        var index = 0
        var digit = 0
        val result = StringBuilder((bytes.size + 7) * 8 / 5)

        while (i < bytes.size) {
            val curr = bytes[i].toInt() and 0xFF
            if (index > 3) {
                val next = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
                digit = (curr and (0xFF shr index)) shl (index - 3)
                digit = digit or (next shr (11 - index))
                i++
            } else {
                digit = (curr shr (3 - index)) and 0x1F
            }
            index = (index + 5) % 8
            if (index == 0) i++
            result.append(alphabet[digit])
        }
        return result.toString()
    }
}
