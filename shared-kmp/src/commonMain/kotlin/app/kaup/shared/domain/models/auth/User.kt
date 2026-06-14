package app.kaup.shared.domain.models.auth

data class User(
    val id: String,
    val name: String,
    val role: Role,
    val pinHash: String
)
