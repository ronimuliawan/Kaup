package app.kaup.shared.domain

import app.kaup.shared.models.Role
import app.kaup.shared.models.Permission

object RoleDefaults {
    val defaultPermissions: Map<Role, Set<Permission>> = mapOf(
        Role.OWNER to Permission.entries.toSet(),
        Role.MANAGER to Permission.entries.toSet(),
        Role.CASHIER to setOf(
            Permission.POS_ACCESS,
            Permission.INVENTORY_VIEW
        ),
        Role.WAITER to setOf(
            Permission.POS_ACCESS
        )
    )
}
