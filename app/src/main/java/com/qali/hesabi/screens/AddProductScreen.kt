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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.rememberCoroutineScope
import com.qali.hesabi.data.Product
import com.qali.hesabi.ui.ProductViewModel
import com.qali.hesabi.util.toEnglishNumbers
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AddProductScreen(navController: NavController, productViewModel: ProductViewModel, productId: Int? = null) {
    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf(0.0) }
    var quantity by rememberSaveable { mutableStateOf(0) }
    var barcode by rememberSaveable { mutableStateOf("") }
    var isEdit by rememberSaveable { mutableStateOf(false) }
    var initialized by rememberSaveable { mutableStateOf(false) }

    // Add debugging for form state
    LaunchedEffect(name, price, quantity, barcode) {
        Log.d(
            "AddProductScreen",
            "Form state changed - name: $name, price: $price, " +
                "quantity: $quantity, barcode: $barcode"
        )
    }

    LaunchedEffect(productId) {
        if (productId != null && !initialized) {
            val product = withContext(Dispatchers.IO) { productViewModel.getProductById(productId) }
            product?.let {
                name = it.name
                price = it.price
                quantity = it.quantity
                barcode = it.barcode
                isEdit = true
                initialized = true
                Log.d(
                    "AddProductScreen",
                    "Product loaded for editing - name: $name, price: $price, " +
                        "quantity: $quantity, barcode: $barcode"
                )
            }
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            Log.d("AddProductScreen", "Scanner result received: ${result.contents}")
            result.contents?.let { scannedBarcode ->
                Log.d("AddProductScreen", "Barcode scanned: $scannedBarcode")
                Log.d(
                    "AddProductScreen",
                    "Form state before setting barcode - name: $name, price: $price, " +
                        "quantity: $quantity, barcode: $barcode"
                )
                barcode = scannedBarcode
                Log.d(
                    "AddProductScreen",
                    "Form state after setting barcode - name: $name, price: $price, " +
                        "quantity: $quantity, barcode: $barcode"
                )
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isEdit) "ویرایش محصول" else "افزودن محصول جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("نام محصول") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = if (price == 0.0) "" else price.toString(),
            onValueChange = { price = it.toEnglishNumbers().toDoubleOrNull() ?: 0.0 },
            label = { Text("قیمت (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = if (quantity == 0) "" else quantity.toString(),
            onValueChange = { quantity = it.toEnglishNumbers().toIntOrNull() ?: 0 },
            label = { Text("تعداد") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("بارکد") },
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
            IconButton(onClick = {
                val randomBarcode = (1..13).map { _ -> (0..9).random() }.joinToString("")
                barcode = randomBarcode
            }) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "تولید بارکد تصادفی")
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && price > 0.0 && quantity > 0) {
                    if (isEdit && productId != null) {
                        productViewModel.update(
                            Product(
                                id = productId,
                                name = name,
                                price = price,
                                quantity = quantity,
                                barcode = barcode
                            )
                        )
                    } else {
                        productViewModel.insert(
                            Product(
                                name = name,
                                price = price,
                                quantity = quantity,
                                barcode = barcode
                            )
                        )
                    }
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره محصول")
        }
    }
}