package app.kaup.shared.domain

import app.kaup.shared.models.*
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryEngineTest {
    
    private val engine = InventoryEngine()
    
    @Test
    fun `calculates stock from sequential IN and OUT events`() {
        val movements = listOf(
            createMovement("1", MovementDirection.IN, 10.0, "2026-06-13T10:00:00Z"),
            createMovement("2", MovementDirection.OUT, 3.0, "2026-06-13T10:15:00Z")
        )
        assertEquals(7.0, engine.computeStock(movements))
    }

    @Test
    fun `handles negative stock gracefully`() {
        val movements = listOf(
            createMovement("1", MovementDirection.IN, 5.0, "2026-06-13T10:00:00Z"),
            createMovement("2", MovementDirection.OUT, 8.0, "2026-06-13T10:15:00Z")
        )
        assertEquals(-3.0, engine.computeStock(movements))
    }

    @Test
    fun `sorts out of order events chronologically`() {
        // Device A syncs late, so its older event is at the end of the list
        val movements = listOf(
            createMovement("3", MovementDirection.OUT, 2.0, "2026-06-13T10:30:00Z"), // Late arrival
            createMovement("1", MovementDirection.IN, 10.0, "2026-06-13T10:00:00Z"), // Early
            createMovement("2", MovementDirection.OUT, 3.0, "2026-06-13T10:15:00Z")  // Middle
        )
        assertEquals(5.0, engine.computeStock(movements)) // 10 - 3 - 2 = 5
    }

    @Test
    fun `computes stock as of a specific point in time`() {
        val movements = listOf(
            createMovement("1", MovementDirection.IN, 10.0, "2026-06-13T10:00:00Z"),
            createMovement("2", MovementDirection.OUT, 3.0, "2026-06-13T10:15:00Z"),
            createMovement("3", MovementDirection.OUT, 2.0, "2026-06-13T10:30:00Z")
        )
        
        val targetTime = Instant.parse("2026-06-13T10:20:00Z")
        // Should only include event 1 and 2
        assertEquals(7.0, engine.computeStockAsOf(movements, targetTime))
    }
    
    private fun createMovement(
        id: String, 
        direction: MovementDirection, 
        quantity: Double, 
        timestampStr: String
    ) = StockMovement(
        id = id,
        itemId = "item_123",
        type = if (direction == MovementDirection.IN) MovementType.RECEIVING else MovementType.SALE,
        direction = direction,
        quantity = quantity,
        transactionId = null,
        deviceId = "dev_1",
        timestamp = Instant.parse(timestampStr)
    )
}
