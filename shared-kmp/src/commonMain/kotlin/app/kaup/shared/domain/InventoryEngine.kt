package app.kaup.shared.domain

import app.kaup.shared.models.MovementDirection
import app.kaup.shared.models.StockMovement
import kotlinx.datetime.Instant

class InventoryEngine {
    
    fun computeStock(movements: List<StockMovement>): Double {
        return movements
            .sortedBy { it.timestamp }
            .fold(0.0) { acc, movement ->
                when (movement.direction) {
                    MovementDirection.IN -> acc + movement.quantity
                    MovementDirection.OUT -> acc - movement.quantity
                }
            }
    }

    fun computeStockAsOf(movements: List<StockMovement>, targetTime: Instant): Double {
        return movements
            .filter { it.timestamp <= targetTime }
            .sortedBy { it.timestamp }
            .fold(0.0) { acc, movement ->
                when (movement.direction) {
                    MovementDirection.IN -> acc + movement.quantity
                    MovementDirection.OUT -> acc - movement.quantity
                }
            }
    }
}
