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
import kotlinx.coroutines.launch

@Composable
fun AddProductScreen(navController: NavController, productViewModel: ProductViewModel) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0.0) }
    var quantity by remember { mutableStateOf(0) }
    var barcode by remember { mutableStateOf("") }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let {
                barcode = it
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "افزودن محصول جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("نام محصول") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = price.toString(),
            onValueChange = { price = it.toDoubleOrNull() ?: 0.0 },
            label = { Text("قیمت (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = quantity.toString(),
            onValueChange = { quantity = it.toIntOrNull() ?: 0 },
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
            IconButton(onClick = {
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                options.setPrompt("Scan a barcode")
                options.setCameraId(0)
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                scannerLauncher.launch(options)
            }) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "تولید بارکد")
            }
            IconButton(onClick = {
                val randomBarcode = (0..9).shuffled().joinToString("").substring(0, 13)
                barcode = randomBarcode
            }) {
                Icon(Icons.Filled.Camera, contentDescription = "اسکن بارکد")
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                coroutineScope.launch {
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
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره محصول")
        }
    }
}