package app.kaup.shared.models

data class SaleTotals(
    val subtotal: Money,
    val discountTotal: Money,
    val taxTotal: Money,
    val finalTotal: Money
)
