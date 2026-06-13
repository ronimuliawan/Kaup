package app.kaup.shared.models.update

sealed class UpdateResult {
    data object UpToDate : UpdateResult()
    data class UpdateAvailable(
        val version: String,
        val releaseNotes: String,
        val downloadUrl: String
    ) : UpdateResult()
    data class Error(val reason: String) : UpdateResult()
}
