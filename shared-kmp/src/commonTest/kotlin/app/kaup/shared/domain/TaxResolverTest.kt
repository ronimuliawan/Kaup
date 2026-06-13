package app.kaup.shared.domain

import app.kaup.shared.models.TaxRate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaxResolverTest {

    private val defaultTaxes = listOf(
        TaxRate("vat_standard", 0.10, isInclusive = false),
        TaxRate("city_tax", 0.02, isInclusive = false)
    )
    
    private val resolver = TaxResolver(defaultTaxes)

    @Test
    fun `inherits store defaults when product overrides are null`() {
        val resolved = resolver.resolve(productTaxOverrides = null)
        assertEquals(2, resolved.size)
        assertEquals(defaultTaxes, resolved)
    }

    @Test
    fun `product overrides store defaults`() {
        val overrideTaxes = listOf(TaxRate("reduced_vat", 0.05, isInclusive = false))
        val resolved = resolver.resolve(productTaxOverrides = overrideTaxes)
        assertEquals(1, resolved.size)
        assertEquals("reduced_vat", resolved.first().id)
    }

    @Test
    fun `explicitly tax free product bypasses defaults`() {
        val resolved = resolver.resolve(productTaxOverrides = emptyList())
        assertTrue(resolved.isEmpty())
    }

    @Test
    fun `zero rate product preserves tax object`() {
        val zeroRate = listOf(TaxRate("zero_vat", 0.0, isInclusive = false))
        val resolved = resolver.resolve(productTaxOverrides = zeroRate)
        assertEquals(1, resolved.size)
        assertEquals(0.0, resolved.first().rate)
    }

    @Test
    fun `customer exemption bypasses defaults`() {
        val resolved = resolver.resolve(
            productTaxOverrides = null,
            isCustomerTaxExempt = true
        )
        assertTrue(resolved.isEmpty())
    }

    @Test
    fun `order exemption bypasses product overrides`() {
        val overrideTaxes = listOf(TaxRate("reduced_vat", 0.05, isInclusive = false))
        val resolved = resolver.resolve(
            productTaxOverrides = overrideTaxes,
            isOrderTaxExempt = true
        )
        assertTrue(resolved.isEmpty())
    }
}
