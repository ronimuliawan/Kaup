package app.kaup.shared.domain

import app.kaup.shared.models.*
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConflictResolverTest {

    private val resolver = ConflictResolver()

    @Test
    fun `sorts exact timestamp ties deterministically by deviceId`() {
        val time = "2026-06-13T10:00:00Z"
        val m1 = createMovement("1", MovementDirection.IN, 1.0, time, "device_C")
        val m2 = createMovement("2", MovementDirection.IN, 1.0, time, "device_A")
        val m3 = createMovement("3", MovementDirection.IN, 1.0, time, "device_B")

        val unsorted = listOf(m1, m2, m3)
        val sorted = resolver.sortDeterministically(unsorted)

        assertEquals("device_A", sorted[0].deviceId)
        assertEquals("device_B", sorted[1].deviceId)
        assertEquals("device_C", sorted[2].deviceId)
    }

    @Test
    fun `detects when two devices sell the last unit offline`() {
        // Initial stock is 1
        val mIn = createMovement("in_1", MovementDirection.IN, 1.0, "2026-06-13T10:00:00Z", "dev_manager")
        
        // Both devices sell 1 unit at nearly the same time
        val mSaleA = createMovement("out_A", MovementDirection.OUT, 1.0, "2026-06-13T10:10:00Z", "dev_A")
        val mSaleB = createMovement("out_B", MovementDirection.OUT, 1.0, "2026-06-13T10:15:00Z", "dev_B")

        val violations = resolver.detectNegativeStockViolations(listOf(mIn, mSaleB, mSaleA))
        
        assertEquals(1, violations.size)
        // The chronologically later sale (dev_B) is the one that breached zero
        assertEquals("out_B", violations[0].id)
    }

    @Test
    fun `gracefully ignores expected fluctuations that stay positive`() {
        val movements = listOf(
            createMovement("1", MovementDirection.IN, 10.0, "2026-06-13T10:00:00Z", "dev_1"),
            createMovement("2", MovementDirection.OUT, 5.0, "2026-06-13T10:05:00Z", "dev_1"),
            createMovement("3", MovementDirection.OUT, 4.0, "2026-06-13T10:10:00Z", "dev_1")
        )
        
        val violations = resolver.detectNegativeStockViolations(movements)
        assertTrue(violations.isEmpty())
    }

    private fun createMovement(
        id: String, 
        direction: MovementDirection, 
        quantity: Double, 
        timestampStr: String,
        deviceId: String
    ) = StockMovement(
        id = id,
        itemId = "item_123",
        type = if (direction == MovementDirection.IN) MovementType.RECEIVING else MovementType.SALE,
        direction = direction,
        quantity = quantity,
        transactionId = null,
        deviceId = deviceId,
        timestamp = Instant.parse(timestampStr)
    )
}
