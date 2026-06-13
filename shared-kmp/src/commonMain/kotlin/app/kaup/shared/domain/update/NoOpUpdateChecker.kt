package app.kaup.shared.domain.update

import app.kaup.shared.models.update.UpdateResult

class NoOpUpdateChecker : UpdateChecker {
    override suspend fun checkForUpdate(): UpdateResult {
        return UpdateResult.UpToDate
    }
}
