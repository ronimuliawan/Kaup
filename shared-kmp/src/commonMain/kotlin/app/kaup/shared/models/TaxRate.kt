package app.kaup.shared.models

data class TaxRate(
    val id: String,
    val rate: Double,
    val isInclusive: Boolean
)
