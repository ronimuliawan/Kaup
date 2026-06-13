package app.kaup.shared.domain.update

import app.kaup.shared.models.update.UpdateResult

interface UpdateChecker {
    suspend fun checkForUpdate(): UpdateResult
}
