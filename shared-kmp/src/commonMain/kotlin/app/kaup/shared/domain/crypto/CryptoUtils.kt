package app.kaup.shared.domain.crypto

expect object CryptoUtils {
    fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray
}
