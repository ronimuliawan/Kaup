package app.kaup.shared.domain.models.auth

enum class Permission {
    POS_CHECKOUT,
    POS_VOID_TRANSACTION,
    POS_APPLY_DISCOUNT,
    INVENTORY_VIEW,
    INVENTORY_EDIT,
    REPORTS_VIEW,
    SETTINGS_MANAGE_USERS,
    SETTINGS_MANAGE_APP
}

fun Role.getDefaultPermissions(): Set<Permission> {
    return when (this) {
        Role.OWNER -> Permission.entries.toSet()
        Role.MANAGER -> Permission.entries.toSet() - Permission.SETTINGS_MANAGE_USERS
        Role.CASHIER -> setOf(Permission.POS_CHECKOUT)
        Role.WAITER -> setOf(Permission.POS_CHECKOUT)
    }
}
