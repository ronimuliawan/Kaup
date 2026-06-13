package app.kaup.shared.domain

import app.kaup.shared.models.*
import kotlin.math.roundToLong

class SalesCalculator {
    fun calculateTotals(items: List<LineItem>, cartDiscounts: List<Discount> = emptyList()): SaleTotals {
        var subtotalMinorUnits = 0L
        var discountTotalMinorUnits = 0L
        
        // 1. Calculate per-item subtotals and item discounts
        val itemPostDiscountTotals = items.map { item ->
            val rawSubtotal = (item.unitPrice.minorUnits * item.quantity).roundToLong()
            subtotalMinorUnits += rawSubtotal
            
            var itemDiscount = 0L
            for (discount in item.discounts) {
                val amount = when (discount) {
                    is Discount.Percentage -> (rawSubtotal * discount.rate).roundToLong()
                    is Discount.FixedAmount -> discount.amount.minorUnits
                }
                itemDiscount += amount
            }
            // Prevent discount from exceeding the subtotal
            if (itemDiscount > rawSubtotal) itemDiscount = rawSubtotal
            
            discountTotalMinorUnits += itemDiscount
            rawSubtotal - itemDiscount
        }

        // Sum up to get the cart amount before cart-level discounts
        val cartPreTaxTotal = itemPostDiscountTotals.sum()

        // 2. Cart Discounts (distributed globally)
        var globalCartDiscount = 0L
        for (discount in cartDiscounts) {
            val amount = when (discount) {
                is Discount.Percentage -> (cartPreTaxTotal * discount.rate).roundToLong()
                is Discount.FixedAmount -> discount.amount.minorUnits
            }
            globalCartDiscount += amount
        }
        if (globalCartDiscount > cartPreTaxTotal) globalCartDiscount = cartPreTaxTotal
        
        discountTotalMinorUnits += globalCartDiscount

        // The total value remaining to be taxed
        val totalTaxableValue = cartPreTaxTotal - globalCartDiscount

        // 3. Taxes
        val cartDiscountRatio = if (cartPreTaxTotal > 0) {
            globalCartDiscount.toDouble() / cartPreTaxTotal.toDouble()
        } else {
            0.0
        }

        var exclusiveTaxSum = 0L
        var inclusiveTaxSum = 0L

        items.forEachIndexed { index, item ->
            val postItemDiscount = itemPostDiscountTotals[index]
            val proportionalCartDiscount = (postItemDiscount * cartDiscountRatio).roundToLong()
            val finalTaxableItemValue = postItemDiscount - proportionalCartDiscount

            item.taxes.forEach { tax ->
                if (tax.isInclusive) {
                    // Tax is already inside the price
                    val taxAmount = finalTaxableItemValue - (finalTaxableItemValue / (1.0 + tax.rate)).roundToLong()
                    inclusiveTaxSum += taxAmount
                } else {
                    // Tax is added on top
                    val taxAmount = (finalTaxableItemValue * tax.rate).roundToLong()
                    exclusiveTaxSum += taxAmount
                }
            }
        }

        val taxTotalMinorUnits = inclusiveTaxSum + exclusiveTaxSum

        // Final total only adds exclusive tax, because inclusive tax is already in the price
        val finalTotalMinorUnits = totalTaxableValue + exclusiveTaxSum

        return SaleTotals(
            subtotal = Money(subtotalMinorUnits),
            discountTotal = Money(discountTotalMinorUnits),
            taxTotal = Money(taxTotalMinorUnits),
            finalTotal = Money(finalTotalMinorUnits)
        )
    }
}
