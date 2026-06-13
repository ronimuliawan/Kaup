package app.kaup.shared.models

data class LineItem(
    val productId: String,
    val unitPrice: Money,
    val quantity: Double,
    val discounts: List<Discount> = emptyList(),
    val taxes: List<TaxRate> = emptyList()
)
