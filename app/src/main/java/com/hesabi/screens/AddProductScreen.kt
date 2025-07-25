package com.hesabi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
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

@Composable
fun AddProductScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

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
            value = price,
            onValueChange = { price = it.filter { c -> c.isDigit() } },
            label = { Text("قیمت (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
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
            IconButton(onClick = { /* TODO: Generate barcode */ }) {
                Icon(Icons.Filled.QrCode, contentDescription = "تولید بارکد")
            }
            IconButton(onClick = { /* TODO: Scan barcode */ }) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "اسکن بارکد")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Save product */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره محصول")
        }
    }
}