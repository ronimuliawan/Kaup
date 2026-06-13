package app.kaup.shared.domain.crypto

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual object CryptoUtils {
    actual fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        val algorithm = "HmacSHA1"
        val mac = Mac.getInstance(algorithm)
        val keySpec = SecretKeySpec(key, algorithm)
        mac.init(keySpec)
        return mac.doFinal(data)
    }
}
