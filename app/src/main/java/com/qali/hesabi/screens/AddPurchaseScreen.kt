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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit
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
import com.qali.hesabi.ui.ProductViewModel
import com.qali.hesabi.data.Purchase
import com.qali.hesabi.ui.PurchaseViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.qali.hesabi.data.PurchaseItem
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseScreen(navController: NavController, productViewModel: ProductViewModel, purchaseViewModel: PurchaseViewModel, purchaseId: Int? = null) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var showProductDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var purchaseItems by remember { mutableStateOf(listOf<PurchaseItem>()) }
    var isEdit by remember { mutableStateOf(false) }
    var editingItemIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(purchaseId) {
        if (purchaseId != null) {
            val purchase = withContext(Dispatchers.IO) { purchaseViewModel.getPurchaseById(purchaseId) }
            purchase?.let {
                purchaseItems = it.items
                isEdit = true
            }
        }
    }
    
    val products by productViewModel.allProducts.collectAsState(initial = emptyList())
    val filteredProducts = products.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.barcode.contains(searchQuery, ignoreCase = true)
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                val foundProduct = products.find { it.barcode == barcode }
                if (foundProduct != null) {
                    selectedProduct = foundProduct
                    price = foundProduct.price.toString()
                }
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isEdit) "ویرایش خرید" else "افزودن خرید جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        
        // Product selection row
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = selectedProduct?.name ?: "",
                onValueChange = {},
                label = { Text("محصول") },
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
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
            label = { Text("تعداد") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = price,
            onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("قیمت (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                val qty = quantity.toIntOrNull() ?: 0
                val prc = price.toDoubleOrNull() ?: 0.0
                if (selectedProduct != null && qty > 0 && prc > 0.0) {
                    val item = PurchaseItem(
                        productId = selectedProduct!!.id,
                        productName = selectedProduct!!.name,
                        quantity = qty,
                        price = prc,
                        barcode = selectedProduct!!.barcode
                    )
                    if (editingItemIndex != null) {
                        purchaseItems = purchaseItems.toMutableList().also { it[editingItemIndex!!] = item }
                        editingItemIndex = null
                    } else {
                        purchaseItems = purchaseItems + item
                    }
                    selectedProduct = null
                    quantity = ""
                    price = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (editingItemIndex != null) "بروزرسانی آیتم" else "افزودن به لیست خرید")
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (purchaseItems.isNotEmpty()) {
            Text("لیست اقلام خریداری شده:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.height(120.dp)) {
                itemsIndexed(purchaseItems) { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${item.productName} - تعداد: ${item.quantity} - قیمت: ${item.price} تومان", modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            selectedProduct = Product(
                                id = item.productId,
                                name = item.productName,
                                price = item.price,
                                quantity = 0, // Not tracked in PurchaseItem
                                barcode = item.barcode
                            )
                            quantity = item.quantity.toString()
                            price = item.price.toString()
                            editingItemIndex = index
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "ویرایش آیتم")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Button(
            onClick = {
                if (purchaseItems.isNotEmpty()) {
                    coroutineScope.launch {
                        if (isEdit && purchaseId != null) {
                            purchaseViewModel.update(
                                Purchase(
                                    id = purchaseId,
                                    total = purchaseItems.sumOf { it.price * it.quantity },
                                    items = purchaseItems
                                )
                            )
                        } else {
                            purchaseViewModel.insert(
                                Purchase(
                                    total = purchaseItems.sumOf { it.price * it.quantity },
                                    items = purchaseItems
                                )
                            )
                        }
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره خرید")
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
                                    selectedProduct = product
                                    price = product.price.toString()
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