package com.qali.hesabi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qali.hesabi.HesabiApplication
import com.qali.hesabi.screens.ProductsScreen
import com.qali.hesabi.screens.SalesScreen
import com.qali.hesabi.screens.PurchasesScreen
import com.qali.hesabi.screens.WalletScreen
import com.qali.hesabi.screens.AddProductScreen
import com.qali.hesabi.screens.AddSaleScreen
import com.qali.hesabi.screens.AddPurchaseScreen
import com.qali.hesabi.screens.AddWalletTransactionScreen
import com.qali.hesabi.ui.ProductViewModel
import com.qali.hesabi.ui.ProductViewModelFactory

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Products : Screen("products", "محصولات", Icons.Filled.ShoppingCart)
    object Sales : Screen("sales", "فروش‌ها", Icons.Filled.List)
    object Purchases : Screen("purchases", "خریدها", Icons.Filled.Home)
    object Wallet : Screen("wallet", "کیف پول", Icons.Filled.ShoppingCart)
    object AddProduct : Screen("add_product", "افزودن محصول")
    object AddSale : Screen("add_sale", "افزودن فروش")
    object AddPurchase : Screen("add_purchase", "افزودن خرید")
    object AddWalletTransaction : Screen("add_wallet_transaction", "افزودن تراکنش")
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
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory((context.applicationContext as HesabiApplication).database.productDao())
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = { navController.navigate(screen.route) },
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
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
            composable(Screen.Products.route) { ProductsScreen(navController, productViewModel) }
            composable(Screen.Sales.route) { SalesScreen(navController) }
            composable(Screen.Purchases.route) { PurchasesScreen(navController) }
            composable(Screen.Wallet.route) { WalletScreen(navController) }
            composable(Screen.AddProduct.route) { AddProductScreen(navController, productViewModel) }
            composable(Screen.AddSale.route) { AddSaleScreen(navController) }
            composable(Screen.AddPurchase.route) { AddPurchaseScreen(navController) }
            composable(Screen.AddWalletTransaction.route) { AddWalletTransactionScreen(navController) }
        }
    }
}