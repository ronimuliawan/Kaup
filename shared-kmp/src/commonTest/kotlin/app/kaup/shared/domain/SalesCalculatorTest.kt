package app.kaup.shared.domain

import app.kaup.shared.models.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SalesCalculatorTest {

    private val calculator = SalesCalculator()

    @Test
    fun `empty cart should return zero totals`() {
        val totals = calculator.calculateTotals(emptyList())
        assertEquals(Money.ZERO, totals.subtotal)
        assertEquals(Money.ZERO, totals.discountTotal)
        assertEquals(Money.ZERO, totals.taxTotal)
        assertEquals(Money.ZERO, totals.finalTotal)
    }

    @Test
    fun `basic line items without discounts or taxes`() {
        val items = listOf(
            LineItem("item1", Money(1000L), 2.0), // $20.00
            LineItem("item2", Money(500L), 1.0)   // $5.00
        )
        val totals = calculator.calculateTotals(items)
        assertEquals(Money(2500L), totals.subtotal)
        assertEquals(Money(0L), totals.discountTotal)
        assertEquals(Money(0L), totals.taxTotal)
        assertEquals(Money(2500L), totals.finalTotal)
    }

    @Test
    fun `fractional quantities calculate correctly`() {
        val items = listOf(
            LineItem("flour", Money(1000L), 1.5) // $10.00/kg * 1.5kg = $15.00
        )
        val totals = calculator.calculateTotals(items)
        assertEquals(Money(1500L), totals.subtotal)
        assertEquals(Money(1500L), totals.finalTotal)
    }

    @Test
    fun `item level percentage discount`() {
        val items = listOf(
            LineItem(
                "item", 
                Money(1000L), 
                1.0, 
                discounts = listOf(Discount.Percentage(0.10)) // 10% off
            )
        )
        val totals = calculator.calculateTotals(items)
        assertEquals(Money(1000L), totals.subtotal)
        assertEquals(Money(100L), totals.discountTotal) // 10% of 1000
        assertEquals(Money(900L), totals.finalTotal)
    }

    @Test
    fun `cart level fixed discount`() {
        val items = listOf(
            LineItem("item1", Money(1000L), 1.0),
            LineItem("item2", Money(1000L), 1.0)
        )
        val totals = calculator.calculateTotals(items, listOf(Discount.FixedAmount(Money(500L))))
        assertEquals(Money(2000L), totals.subtotal)
        assertEquals(Money(500L), totals.discountTotal)
        assertEquals(Money(1500L), totals.finalTotal)
    }

    @Test
    fun `exclusive tax increases final total`() {
        val items = listOf(
            LineItem(
                "item", 
                Money(1000L), 
                1.0, 
                taxes = listOf(TaxRate("vat", 0.10, isInclusive = false)) // 10% exclusive
            )
        )
        val totals = calculator.calculateTotals(items)
        assertEquals(Money(1000L), totals.subtotal)
        assertEquals(Money(100L), totals.taxTotal) // 10% of 1000
        assertEquals(Money(1100L), totals.finalTotal) // 1000 + 100
    }

    @Test
    fun `inclusive tax does not increase final total`() {
        val items = listOf(
            LineItem(
                "item", 
                Money(1100L), // Price including tax
                1.0, 
                taxes = listOf(TaxRate("vat", 0.10, isInclusive = true)) // 10% inclusive
            )
        )
        val totals = calculator.calculateTotals(items)
        assertEquals(Money(1100L), totals.subtotal)
        // Inclusive tax amount = 1100 - (1100 / 1.1) = 1100 - 1000 = 100
        assertEquals(Money(100L), totals.taxTotal) 
        assertEquals(Money(1100L), totals.finalTotal) // Customer still pays 1100
    }
}
