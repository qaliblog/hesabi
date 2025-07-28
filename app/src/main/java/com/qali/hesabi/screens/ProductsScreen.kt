package com.qali.hesabi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavController
import com.qali.hesabi.components.ProductCard
import androidx.compose.ui.unit.dp
import com.qali.hesabi.navigation.Screen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.qali.hesabi.ui.ProductViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.qali.hesabi.ui.SaleViewModel
import com.qali.hesabi.ui.PurchaseViewModel
import com.qali.hesabi.data.Product
import com.qali.hesabi.data.Sale
import com.qali.hesabi.data.Purchase
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.width
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun ProductsScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    saleViewModel: SaleViewModel,
    purchaseViewModel: PurchaseViewModel
) {
    val products by productViewModel.allProducts.collectAsState(initial = emptyList())
    val sales by saleViewModel.allSales.collectAsState(initial = emptyList())
    val purchases by purchaseViewModel.allPurchases.collectAsState(initial = emptyList())
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.background
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {
        androidx.compose.material3.Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "محصولات",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp)
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onEdit = { navController.navigate(Screen.AddProduct.route + "/${product.id}") },
                    onDelete = { productViewModel.delete(it) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Warehouse section
        androidx.compose.material3.Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Filled.Warehouse, contentDescription = "Warehouse", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("انبار محصولات", style = MaterialTheme.typography.titleLarge)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                products.forEach { product ->
                    val productPurchases = purchases.filter { it.items.any { item -> item.productId == product.id } }
                    val productSales = sales.filter { it.products.any { item -> item.productId == product.id } }
                    val totalPurchased = productPurchases.sumOf { it.items.find { item -> item.productId == product.id }?.quantity ?: 0 }
                    val totalSold = productSales.sumOf { it.products.find { item -> item.productId == product.id }?.quantity ?: 0 }
                    val stock = totalPurchased - totalSold
                    val expanded = remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(product.name, modifier = Modifier.weight(1f))
                        Text("موجودی: $stock", color = if (stock > 0) Color.Green else Color.Red)
                        IconButton(onClick = { expanded.value = !expanded.value }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Show History")
                        }
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        productPurchases.forEach { purchase ->
                            val qty = purchase.items.find { it.productId == product.id }?.quantity ?: 0
                            DropdownMenuItem(text = { Text("+ $qty (خرید)", color = Color.Green) }, onClick = {})
                        }
                        productSales.forEach { sale ->
                            val qty = sale.products.find { it.productId == product.id }?.quantity ?: 0
                            DropdownMenuItem(text = { Text("- $qty (فروش)", color = Color.Red) }, onClick = {})
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate(Screen.AddProduct.route) },
            modifier = Modifier
                .align(Alignment.End)
                .height(56.dp)
                .requiredWidth(220.dp),
            shape = MaterialTheme.shapes.medium,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("افزودن محصول جدید", style = MaterialTheme.typography.titleLarge)
        }
    }
}