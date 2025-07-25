package com.hesabi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddWalletTransactionScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(0) } // 0: INCOME, 1: EXPENSE
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "افزودن تراکنش جدید", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("مبلغ (تومان)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = type == 0,
                onClick = { type = 0 }
            )
            Text("درآمد")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = type == 1,
                onClick = { type = 1 }
            )
            Text("هزینه")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("توضیحات") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Save transaction */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("ذخیره تراکنش")
        }
    }
}