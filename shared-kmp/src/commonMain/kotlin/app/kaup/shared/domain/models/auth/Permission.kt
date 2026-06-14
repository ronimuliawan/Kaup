package app.kaup.shared.domain.models.auth

enum class Permission {
    // POS
    POS_CHECKOUT,
    POS_TABLE_MANAGEMENT,
    POS_OPEN_SHIFT,
    POS_CLOSE_SHIFT,
    POS_APPLY_DISCOUNT,
    POS_DISCOUNT_ABOVE_X,
    POS_VOID_TRANSACTION,
    POS_ISSUE_REFUND,
    POS_OVERRIDE_PRICE,
    
    // Inventory
    INVENTORY_VIEW,
    INVENTORY_ADD_ITEM,
    INVENTORY_EDIT_ITEM,
    INVENTORY_DELETE_ITEM,
    INVENTORY_RECEIVE_STOCK,
    INVENTORY_TRANSFER_STOCK,
    
    // Customers
    CUSTOMERS_VIEW,
    CUSTOMERS_ADD,
    CUSTOMERS_EDIT,
    CUSTOMERS_DELETE,
    
    // Reports
    REPORTS_VIEW_SALES,
    REPORTS_VIEW_INVENTORY,
    REPORTS_VIEW_FINANCIAL,
    REPORTS_EXPORT,
    
    // Users
    USERS_VIEW,
    USERS_ADD,
    USERS_EDIT,
    USERS_DELETE,
    
    // Settings
    SETTINGS_BACKEND,
    SETTINGS_FEATURE_FLAGS,
    SETTINGS_HOUSEKEEPING,
    SETTINGS_TAX
}

fun Role.getDefaultPermissions(): Set<Permission> {
    return when (this) {
        Role.OWNER -> Permission.entries.toSet()
        Role.MANAGER -> Permission.entries.toSet() - setOf(
            Permission.USERS_VIEW,
            Permission.USERS_ADD,
            Permission.USERS_EDIT,
            Permission.USERS_DELETE
        )
        Role.CASHIER -> setOf(
            Permission.POS_CHECKOUT,
            Permission.POS_OPEN_SHIFT,
            Permission.POS_CLOSE_SHIFT,
            Permission.POS_APPLY_DISCOUNT,
            Permission.CUSTOMERS_VIEW,
            Permission.CUSTOMERS_ADD
        )
        Role.CREW -> setOf(
            Permission.POS_TABLE_MANAGEMENT,
            Permission.POS_CHECKOUT,
            Permission.POS_APPLY_DISCOUNT
        )
    }
}
