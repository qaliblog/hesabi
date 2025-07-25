package com.hesabi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hesabi.components.PurchaseCard
import com.hesabi.data.SampleData

@Composable
fun PurchasesScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "خریدها",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SampleData.purchases.size) { index ->
                val purchase = SampleData.purchases[index]
                PurchaseCard(purchase = purchase)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: Navigate to AddPurchaseScreen */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("افزودن خرید جدید")
        }
    }
}