package com.qali.hesabi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import com.qali.hesabi.data.Product
import com.qali.hesabi.data.Sale
import com.qali.hesabi.data.SaleItem
import com.qali.hesabi.ui.ProductViewModel
import com.qali.hesabi.ui.SaleViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(navController: NavController, productViewModel: ProductViewModel, saleViewModel: SaleViewModel, saleId: Int? = null) {
    var buyerName by remember { mutableStateOf("") }
    var selectedProducts by remember { mutableStateOf<List<SaleItem>>(emptyList()) }
    var showProductDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var currentQuantity by remember { mutableStateOf("1") }
    var currentPrice by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(saleId) {
        if (saleId != null) {
            val sale = withContext(Dispatchers.IO) { saleViewModel.getSaleById(saleId) }
            sale?.let {
                buyerName = it.buyerName
                selectedProducts = it.products
                isEdit = true
            }
        }
    }
    
    val products by productViewModel.allProducts.collectAsState(initial = emptyList())
    val filteredProducts = products.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.barcode.contains(searchQuery, ignoreCase = true)
    }
    
    val coroutineScope = rememberCoroutineScope()
    val total = selectedProducts.sumOf { it.price * it.quantity }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                val foundProduct = products.find { it.barcode == barcode }
                if (foundProduct != null) {
                    val newItem = SaleItem(
                        productId = foundProduct.id,
                        productName = foundProduct.name,
                        quantity = 1,
                        price = foundProduct.price,
                        barcode = foundProduct.barcode
                    )
                    selectedProducts = selectedProducts + newItem
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isEdit) "ویرایش فروش" else "افزودن فروش جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = buyerName,
            onValueChange = { buyerName = it },
            label = { Text("نام خریدار") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Product selection row
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("انتخاب محصول") },
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val options = ScanOptions()
                    options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                    options.setPrompt("اسکن بارکد محصول")
                    options.setCameraId(0)
                    options.setBeepEnabled(false)
                    options.setBarcodeImageEnabled(true)
                    scannerLauncher.launch(options)
                }
            ) {
                Icon(Icons.Filled.Camera, contentDescription = "اسکن بارکد")
            }
            IconButton(onClick = { showProductDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "انتخاب محصول")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Selected products list
        if (selectedProducts.isNotEmpty()) {
            Text(
                text = "محصولات انتخاب شده:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(selectedProducts) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        if (item.quantity > 1) {
                                            selectedProducts = selectedProducts.map {
                                                if (it == item) it.copy(quantity = it.quantity - 1) else it
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "کاهش تعداد")
                                    }
                                    OutlinedTextField(
                                        value = item.quantity.toString(),
                                        onValueChange = { newValue ->
                                            val newQty = newValue.toIntOrNull() ?: 1
                                            if (newQty > 0) {
                                                selectedProducts = selectedProducts.map {
                                                    if (it == item) it.copy(quantity = newQty) else it
                                                }
                                            }
                                        },
                                        label = { Text("تعداد") },
                                        modifier = Modifier.width(60.dp)
                                    )
                                    IconButton(onClick = {
                                        selectedProducts = selectedProducts.map {
                                            if (it == item) it.copy(quantity = it.quantity + 1) else it
                                        }
                                    }) {
                                        Icon(Icons.Filled.Add, contentDescription = "افزایش تعداد")
                                    }
                                }
                                Text(
                                    text = "قیمت: ${item.price} تومان",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = {
                                    selectedProducts = selectedProducts.filter { it != item }
                                }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "حذف")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "جمع کل: $total تومان",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Button(
            onClick = {
                if (buyerName.isNotBlank() && selectedProducts.isNotEmpty()) {
                    coroutineScope.launch {
                        if (isEdit && saleId != null) {
                            saleViewModel.update(
                                Sale(
                                    id = saleId,
                                    buyerName = buyerName,
                                    total = total,
                                    products = selectedProducts
                                )
                            )
                        } else {
                            saleViewModel.insert(
                                Sale(
                                    buyerName = buyerName,
                                    total = total,
                                    products = selectedProducts
                                )
                            )
                        }
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره فروش")
        }
    }
    
    // Product selection dialog
    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = { Text("انتخاب محصول") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("جستجو در محصولات") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(filteredProducts) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    val newItem = SaleItem(
                                        productId = product.id,
                                        productName = product.name,
                                        quantity = 1,
                                        price = product.price,
                                        barcode = product.barcode
                                    )
                                    selectedProducts = selectedProducts + newItem
                                    showProductDialog = false
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "قیمت: ${product.price} تومان",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "بارکد: ${product.barcode}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showProductDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }
}