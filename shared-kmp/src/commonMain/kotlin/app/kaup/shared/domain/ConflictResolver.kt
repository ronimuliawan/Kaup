package app.kaup.shared.domain

import app.kaup.shared.models.MovementDirection
import app.kaup.shared.models.StockMovement

class ConflictResolver {

    /**
     * Sorts a list of stock movements deterministically to ensure that 
     * identical datasets always resolve to the exact same timeline across all devices.
     */
    fun sortDeterministically(movements: List<StockMovement>): List<StockMovement> {
        return movements.sortedWith(
            compareBy<StockMovement> { it.timestamp }
                .thenBy { it.deviceId }
                .thenBy { it.id }
        )
    }

    /**
     * Replays a list of stock movements and detects the exact events that caused
     * the inventory to drop below zero (e.g., when two devices sell the last unit offline).
     */
    fun detectNegativeStockViolations(movements: List<StockMovement>): List<StockMovement> {
        val sortedMovements = sortDeterministically(movements)
        var currentStock = 0.0
        val violations = mutableListOf<StockMovement>()

        for (movement in sortedMovements) {
            val previousStock = currentStock
            
            when (movement.direction) {
                MovementDirection.IN -> currentStock += movement.quantity
                MovementDirection.OUT -> currentStock -= movement.quantity
            }

            // If this exact movement pushed us from >= 0 into the negative, flag it
            if (previousStock >= 0.0 && currentStock < 0.0) {
                violations.add(movement)
            }
        }

        return violations
    }
}
