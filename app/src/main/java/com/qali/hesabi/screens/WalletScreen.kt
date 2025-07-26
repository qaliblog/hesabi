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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavController
import com.qali.hesabi.components.WalletCard
import com.qali.hesabi.navigation.Screen
import com.qali.hesabi.ui.WalletTransactionViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.requiredWidth

@Composable
fun WalletScreen(navController: NavController, walletTransactionViewModel: WalletTransactionViewModel) {
    val transactions by walletTransactionViewModel.allTransactions.collectAsState(initial = emptyList())
    val overallAmount = transactions.sumOf { if (it.type.name == "INCOME") it.amount else -it.amount }
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
                text = "کیف پول",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp)
            )
        }
        androidx.compose.material3.Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "موجودی کل: ${overallAmount} تومان",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "هنوز تراکنشی ثبت نشده است",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { transaction ->
                    WalletCard(transaction = transaction)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate(Screen.AddWalletTransaction.route) },
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
            Text("افزودن تراکنش جدید", style = MaterialTheme.typography.titleLarge)
        }
    }
}