package app.kaup.shared.models

enum class Permission {
    POS_ACCESS,
    POS_VOID_TRANSACTION,
    POS_CUSTOM_DISCOUNT,
    POS_OPEN_CASH_DRAWER,
    INVENTORY_VIEW,
    INVENTORY_EDIT,
    USERS_VIEW,
    USERS_EDIT,
    REPORTS_VIEW,
    SETTINGS_EDIT
}
