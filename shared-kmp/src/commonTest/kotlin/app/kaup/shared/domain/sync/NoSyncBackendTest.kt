package app.kaup.shared.domain.sync

import app.kaup.shared.models.sync.PendingRecord
import app.kaup.shared.models.sync.SyncOperation
import app.kaup.shared.models.sync.SyncResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoSyncBackendTest {

    private val backend = NoSyncBackend()

    @Test
    fun `isConfigured returns false`() {
        assertFalse(backend.isConfigured())
    }

    @Test
    fun `pushRecords explicitly fails to protect PENDING status`() = runTest {
        val record = PendingRecord(
            id = "1",
            entityType = "Item",
            operation = SyncOperation.INSERT,
            payload = "{}",
            timestamp = Clock.System.now()
        )
        
        val result = backend.pushRecords(listOf(record))
        assertTrue(result is SyncResult.Failure)
        assertEquals("Tier 0 - Local Only mode active", result.reason)
    }

    @Test
    fun `pullUpdates returns empty list`() = runTest {
        val updates = backend.pullUpdates(Clock.System.now())
        assertTrue(updates.isEmpty())
    }

    @Test
    fun `uploadFile returns local path`() = runTest {
        val path = "/local/path/image.jpg"
        val result = backend.uploadFile(path)
        assertEquals(path, result)
    }
}
