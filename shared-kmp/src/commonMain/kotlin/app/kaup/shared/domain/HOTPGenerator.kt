package app.kaup.shared.domain

import app.kaup.shared.domain.crypto.CryptoUtils
import kotlin.math.pow

object HOTPGenerator {
    
    /**
     * Generates an RFC 4226 compliant HOTP code.
     */
    fun generateCode(secret: ByteArray, counter: Long, digits: Int = 6): String {
        val data = counterToByteArray(counter)
        val hmac = CryptoUtils.hmacSha1(secret, data)
        
        // Dynamic Truncation (RFC 4226)
        val offset = hmac.last().toInt() and 0x0F
        
        val binary = ((hmac[offset].toInt() and 0x7F) shl 24) or
                     ((hmac[offset + 1].toInt() and 0xFF) shl 16) or
                     ((hmac[offset + 2].toInt() and 0xFF) shl 8) or
                     (hmac[offset + 3].toInt() and 0xFF)
        
        val otp = binary % 10.0.pow(digits).toInt()
        
        return otp.toString().padStart(digits, '0')
    }

    /**
     * Validates an input code, allowing for counter drift up to [lookAheadWindow].
     * Returns the counter that successfully generated the code, or null if invalid.
     */
    fun validateCode(
        secret: ByteArray, 
        currentCounter: Long, 
        inputCode: String, 
        lookAheadWindow: Int = 5,
        digits: Int = 6
    ): Long? {
        for (i in 0..lookAheadWindow) {
            val targetCounter = currentCounter + i
            val generated = generateCode(secret, targetCounter, digits)
            if (generated == inputCode) {
                return targetCounter
            }
        }
        return null
    }

    private fun counterToByteArray(counter: Long): ByteArray {
        val result = ByteArray(8)
        for (i in 7 downTo 0) {
            result[i] = (counter ushr (8 * (7 - i))).toByte()
        }
        return result
    }
}
