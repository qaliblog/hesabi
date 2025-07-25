package com.hesabi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hesabi.data.SampleData

@Composable
fun AddPurchaseScreen(navController: NavController) {
    var selectedProductIndex by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "افزودن خرید جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = SampleData.products[selectedProductIndex].name,
                    onValueChange = {},
                    label = { Text("محصول") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /* TODO: Scan barcode */ }) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "اسکن بارکد")
                }
            }
            IconButton(onClick = { /* TODO: Add product to purchase */ }) {
                Icon(Icons.Filled.Add, contentDescription = "افزودن محصول")
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
            onValueChange = { price = it.filter { c -> c.isDigit() } },
            label = { Text("قیمت (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Save purchase */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره خرید")
        }
    }
}