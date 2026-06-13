package app.kaup.shared.domain

import app.kaup.shared.models.TaxRate

class TaxResolver(
    private val storeDefaultTaxes: List<TaxRate>
) {
    /**
     * Resolves the applicable tax rates for a product being added to a cart.
     * 
     * @param productTaxOverrides The explicit taxes for this product. Null means "use defaults". Empty list means "tax free".
     * @param isCustomerTaxExempt True if the customer has a tax exemption certificate.
     * @param isOrderTaxExempt True if the cashier manually exempted the entire order.
     */
    fun resolve(
        productTaxOverrides: List<TaxRate>?,
        isCustomerTaxExempt: Boolean = false,
        isOrderTaxExempt: Boolean = false
    ): List<TaxRate> {
        if (isCustomerTaxExempt || isOrderTaxExempt) {
            return emptyList()
        }

        return productTaxOverrides ?: storeDefaultTaxes
    }
}
