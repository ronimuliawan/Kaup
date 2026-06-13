package app.kaup.shared.models

sealed interface Discount {
    data class Percentage(val rate: Double) : Discount
    data class FixedAmount(val amount: Money) : Discount
}
