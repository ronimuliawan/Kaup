package app.kaup.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun KaupAppShell() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "pos"

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = currentRoute == "pos",
                onClick = { navController.navigate("pos") },
                icon = { Text("P") },
                label = { Text("Register") }
            )
            item(
                selected = currentRoute == "inventory",
                onClick = { navController.navigate("inventory") },
                icon = { Text("I") },
                label = { Text("Inventory") }
            )
            item(
                selected = currentRoute == "reports",
                onClick = { navController.navigate("reports") },
                icon = { Text("R") },
                label = { Text("Reports") }
            )
            item(
                selected = currentRoute == "settings",
                onClick = { navController.navigate("settings") },
                icon = { Text("S") },
                label = { Text("Settings") }
            )
        }
    ) {
        NavHost(navController = navController, startDestination = "pos") {
            composable("pos") { DummyScreen("POS Register Placeholder") }
            composable("inventory") { DummyScreen("Inventory Placeholder") }
            composable("reports") { DummyScreen("Reports Placeholder") }
            composable("settings") { DummyScreen("Settings Placeholder") }
        }
    }
}

@Composable
fun DummyScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
