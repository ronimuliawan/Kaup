package app.kaup.shared.domain.update

import app.kaup.shared.models.update.UpdateResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class NoOpUpdateCheckerTest {

    @Test
    fun `NoOpUpdateChecker always returns UpToDate`() = runTest {
        val checker = NoOpUpdateChecker()
        assertTrue(checker.checkForUpdate() is UpdateResult.UpToDate)
    }
}
