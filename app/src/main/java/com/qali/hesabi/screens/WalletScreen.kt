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
import androidx.compose.foundation.layout.width
import com.qali.hesabi.util.JalaliUtils
import java.util.Date
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll

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
                    WalletCard(
                        transaction = transaction,
                        onEdit = { navController.navigate(Screen.AddWalletTransaction.route + "/${transaction.id}") },
                        onDelete = { walletTransactionViewModel.delete(it) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (transactions.isNotEmpty()) {
            DailyExpensesChart(transactions)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Button(
            onClick = { navController.navigate(Screen.AddWalletTransaction.route) },
            modifier = Modifier
                .align(Alignment.End)
                .height(56.dp)
                .width(220.dp),
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

@Composable
fun DailyExpensesChart(transactions: List<com.qali.hesabi.data.WalletTransaction>) {
    // Group by Jalali date string, sum expenses only
    val dailyTotals = transactions
        .filter { it.type == com.qali.hesabi.data.TransactionType.EXPENSE }
        .groupBy { com.qali.hesabi.util.JalaliUtils.toJalaliString(java.util.Date(it.date)) }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedBy { it.first }

    if (dailyTotals.isEmpty()) {
        Text("هیچ هزینه‌ای برای نمایش وجود ندارد", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        return
    }

    val maxAmount = dailyTotals.maxOf { it.second }
    val barWidth = 40f
    val barSpacing = 24f
    val chartHeight = 180f

    androidx.compose.foundation.horizontalScroll(rememberScrollState()) {
        androidx.compose.foundation.Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight.dp)
            .padding(vertical = 8.dp)) {
            dailyTotals.forEachIndexed { idx, (date, amount) ->
                val left = idx * (barWidth + barSpacing)
                val barHeight = (amount / maxAmount * (size.height - 32f)).toFloat()
                drawRect(
                    color = Color(0xFFD32F2F),
                    topLeft = Offset(left, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        date,
                        left + barWidth / 2,
                        size.height - 4f,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 24f
                            color = android.graphics.Color.DKGRAY
                        }
                    )
                    drawText(
                        amount.toInt().toString(),
                        left + barWidth / 2,
                        size.height - barHeight - 8f,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                            color = android.graphics.Color.BLACK
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}