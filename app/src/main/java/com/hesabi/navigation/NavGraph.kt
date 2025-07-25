package com.hesabi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hesabi.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Products : Screen("products", "محصولات", Icons.Filled.ShoppingCart)
    object Sales : Screen("sales", "فروش‌ها", Icons.Filled.ReceiptLong)
    object Purchases : Screen("purchases", "خریدها", Icons.Filled.ShoppingBag)
    object Wallet : Screen("wallet", "کیف پول", Icons.Filled.AccountBalanceWallet)
}

val bottomNavItems = listOf(
    Screen.Products,
    Screen.Sales,
    Screen.Purchases,
    Screen.Wallet
)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = { navController.navigate(screen.route) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Products.route) { ProductsScreen(navController) }
            composable(Screen.Sales.route) { SalesScreen(navController) }
            composable(Screen.Purchases.route) { PurchasesScreen(navController) }
            composable(Screen.Wallet.route) { WalletScreen(navController) }
        }
    }
}